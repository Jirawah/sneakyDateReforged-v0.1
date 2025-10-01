package com.sneakyDateReforged.ms_rdv.service.impl;

import com.sneakyDateReforged.ms_rdv.api.dto.*;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import com.sneakyDateReforged.ms_rdv.mapper.RdvMapper;
import com.sneakyDateReforged.ms_rdv.repository.ParticipantRepository;
import com.sneakyDateReforged.ms_rdv.repository.RdvRepository;
import com.sneakyDateReforged.ms_rdv.service.RdvService;
import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import com.sneakyDateReforged.ms_rdv.infra.notif.NotifClient;
import com.sneakyDateReforged.ms_rdv.infra.notif.dto.NotificationEventDTO;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RdvServiceImpl implements RdvService {

    private final RdvRepository rdvRepository;
    private final ParticipantRepository participantRepository;
    private final NotifClient notifClient;

    @Override
    public RdvDTO create(CreateRdvRequest req) {
        Rdv saved = rdvRepository.save(RdvMapper.from(req));

        // 🔔 push évènement vers ms-notif (non bloquant)
        try {
            var evt = new NotificationEventDTO(
                    "RDV_CREATED",
                    UUID.randomUUID().toString(),
                    saved.getId(),
                    saved.getOrganisateurId(),
                    null,                  // participantId (NA)
                    null,                  // userId (dest direct NA ici)
                    null,                  // invitedUserId (NA)
                    null,                  // recipients (fan-out NA)
                    saved.getDate()  != null ? saved.getDate().toString()  : null,
                    saved.getHeure() != null ? saved.getHeure().toString() : null,
                    saved.getJeu(),
                    null, null,
                    Map.of("source", "ms-rdv")
            );
            notifClient.send(evt);
        } catch (Exception e) {
            log.warn("notif push failed [event=RDV_CREATED, rdvId={}, orgId={}]: {}",
                    saved.getId(), saved.getOrganisateurId(), e.getMessage());
        }

        int participants = (int) participantRepository.countByRdv(saved);
        return RdvMapper.toDTO(saved, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public RdvDTO getById(Long id) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + id));
        int participants = (int) participantRepository.countByRdv(rdv);
        return RdvMapper.toDTO(rdv, participants);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RdvSummaryDTO> listByDate(LocalDate date, String jeu) {
        // tri par propriété d'entité "heure" (le nom du champ JPA, pas le nom de colonne)
        Sort sort = Sort.by(Sort.Order.asc("heure"));

        List<Rdv> list = (jeu == null || jeu.isBlank())
                ? rdvRepository.findAllByDate(date, sort)
                : rdvRepository.findAllByDateAndJeu(date, jeu, sort);

        return list.stream().map(RdvMapper::toSummary).toList();
    }

    @Override
    public RdvDTO update(Long id, UpdateRdvRequest req) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + id));
        // Règle simple: on ne modifie pas un RDV annulé
        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: modification interdite");
        }
        RdvMapper.copy(req, rdv);
        Rdv saved = rdvRepository.save(rdv);
        int participants = (int) participantRepository.countByRdv(saved);
        return RdvMapper.toDTO(saved, participants);
    }

    @Override
    public void delete(Long id) {
        if (!rdvRepository.existsById(id)) throw new EntityNotFoundException("RDV not found: " + id);
        rdvRepository.deleteById(id);
    }

    @Override
    public RdvDTO cancel(Long id) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + id));

        rdv.setStatut(RdvStatus.ANNULE);
        Rdv saved = rdvRepository.save(rdv);

        // Compteur dispo dans le catch si besoin
        int recipientsCount = -1;

        try {
            var participants = participantRepository.findByRdv(saved);

            var recipientSet = participants.stream()
                    .map(p -> p.getUserId())
                    .filter(Objects::nonNull)
                    .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));

            if (saved.getOrganisateurId() != null) {
                recipientSet.add(saved.getOrganisateurId());
            }

            recipientsCount = recipientSet.size();

            var recipients = new java.util.ArrayList<>(recipientSet);

            var evt = new NotificationEventDTO(
                    "RDV_CANCELED",
                    java.util.UUID.randomUUID().toString(),
                    saved.getId(),
                    saved.getOrganisateurId(),
                    null, null, null,
                    recipients,
                    saved.getDate()  != null ? saved.getDate().toString()  : null,
                    saved.getHeure() != null ? saved.getHeure().toString() : null,
                    saved.getJeu(),
                    null, null,
                    java.util.Map.of("source", "ms-rdv")
            );
            notifClient.send(evt);

        } catch (Exception e) {
            log.warn("notif push failed [event=RDV_CANCELED, rdvId={}, orgId={}, recipients={}]: {}",
                    saved.getId(), saved.getOrganisateurId(), recipientsCount, e.getMessage());
            // log.debug("stack:", e);
        }

        int participantsCount = (int) participantRepository.countByRdv(saved);
        return RdvMapper.toDTO(saved, participantsCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RdvSummaryDTO> listByOrganisateur(Long organisateurId) {
        return rdvRepository.findByOrganisateurIdOrderByDateAscHeureAsc(organisateurId)
                .stream()
                .map(RdvMapper::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RdvParticipationDTO> listParticipations(Long userId, ParticipationStatus statusOrNull) {
        var list = (statusOrNull == null)
                ? participantRepository.findByUserId(userId)
                : participantRepository.findByUserIdAndStatutParticipation(userId, statusOrNull);

        return list.stream()
                .map(RdvMapper::toParticipationDTO)
                .toList();
    }
}
