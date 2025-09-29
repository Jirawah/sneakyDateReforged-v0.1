package com.sneakyDateReforged.ms_rdv.repository;

import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RdvRepository extends JpaRepository<Rdv, Long> {
    List<Rdv> findAllByDate(LocalDate date, Sort sort);
    List<Rdv> findAllByDateAndJeu(LocalDate date, String jeu, Sort sort);
    List<Rdv> findByOrganisateurIdOrderByDateAscHeureAsc(Long organisateurId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Rdv r where r.id = :id")
    Optional<Rdv> findByIdForUpdate(@Param("id") Long id);
}
