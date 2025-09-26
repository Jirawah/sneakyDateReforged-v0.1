-- RDV
CREATE TABLE IF NOT EXISTS rdv (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nom VARCHAR(100) NOT NULL,
  date DATE NOT NULL,
  heure TIME NOT NULL,
  jeu VARCHAR(100) NOT NULL,
  statut VARCHAR(50) NOT NULL, -- OUVERT / FERME / ANNULE
  slots INT NOT NULL,
  organisateur_id BIGINT NOT NULL
);

-- PARTICIPANT
CREATE TABLE IF NOT EXISTS participant (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  rdv_id BIGINT NOT NULL,
  role VARCHAR(50) NOT NULL,                 -- ORGANISATEUR / JOUEUR / REMPLACANT
  statut_participation VARCHAR(50) NOT NULL, -- EN_ATTENTE / CONFIRME / REFUSE
  CONSTRAINT fk_participant_rdv FOREIGN KEY (rdv_id) REFERENCES rdv(id) ON DELETE CASCADE
);

-- Index utiles
CREATE INDEX idx_rdv_date ON rdv(date);
CREATE INDEX idx_rdv_jeu_date ON rdv(jeu, date);
CREATE INDEX idx_participant_rdv ON participant(rdv_id);
