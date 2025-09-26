package com.sneakyDateReforged.ms_rdv.repository;

import com.sneakyDateReforged.ms_rdv.domain.Participant;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByRdv(Rdv rdv);
    long countByRdv(Rdv rdv);
    long countByRdvAndStatutParticipation(Rdv rdv, ParticipationStatus status);
    boolean existsByRdvIdAndUserId(Long rdvId, Long userId);
}
