package com.sneakyDateReforged.ms_rdv.repository;

import com.sneakyDateReforged.ms_rdv.domain.Participant;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByRdv(Rdv rdv);
    long countByRdv(Rdv rdv);
    long countByRdvAndStatutParticipation(Rdv rdv, ParticipationStatus status);
    boolean existsByRdvIdAndUserId(Long rdvId, Long userId);
    Optional<Participant> findByRdvIdAndUserId(Long rdvId, Long userId);

    @EntityGraph(attributePaths = {"rdv"})
    List<Participant> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"rdv"})
    List<Participant> findByUserIdAndStatutParticipation(Long userId, ParticipationStatus statut);
}
