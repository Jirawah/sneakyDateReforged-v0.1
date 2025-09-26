package com.sneakyDateReforged.ms_rdv.repository;

import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RdvRepository extends JpaRepository<Rdv, Long> {
    List<Rdv> findAllByDate(LocalDate date);
    List<Rdv> findAllByDateAndJeu(LocalDate date, String jeu);
}
