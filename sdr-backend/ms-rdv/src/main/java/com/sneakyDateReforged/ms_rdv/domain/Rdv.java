package com.sneakyDateReforged.ms_rdv.domain;

import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "rdv",
        indexes = {
                @Index(name = "idx_rdv_date", columnList = "date"),
                @Index(name = "idx_rdv_date_jeu", columnList = "date, jeu") // optionnel
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Rdv {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100) private String nom;

    @Column(nullable=false) private LocalDate date;
    @Column(nullable=false) private LocalTime heure;

    @Column(nullable=false, length=100) private String jeu;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50) private RdvStatus statut;

    @Column(nullable=false) private Integer slots;

    @Column(name="organisateur_id", nullable=false)
    private Long organisateurId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rdv that = (Rdv) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
