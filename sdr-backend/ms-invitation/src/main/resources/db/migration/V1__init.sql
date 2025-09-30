-- Table des invitations à un RDV
CREATE TABLE IF NOT EXISTS invitation (
  id               BIGINT PRIMARY KEY AUTO_INCREMENT,
  rdv_id           BIGINT       NOT NULL,            -- cible: RDV (ms-rdv)
  inviter_user_id  BIGINT       NOT NULL,            -- organisateur ou membre autorisé
  invitee_user_id  BIGINT       NOT NULL,            -- utilisateur invité
  status           VARCHAR(16)  NOT NULL DEFAULT 'PENDING',  -- PENDING/ACCEPTED/DECLINED/CANCELED
  message          VARCHAR(500) NULL,
  created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

-- Un même utilisateur ne peut pas avoir 2 invitations ouvertes pour le même RDV
CREATE UNIQUE INDEX uq_invitation_unique ON invitation (rdv_id, invitee_user_id);

-- Index utiles
CREATE INDEX idx_invitation_rdv_status    ON invitation (rdv_id, status);
CREATE INDEX idx_invitation_invitee_status ON invitation (invitee_user_id, status);
CREATE INDEX idx_invitation_inviter        ON invitation (inviter_user_id);

-- (Pas de FK physiques inter-MS : on reste en intégrité logique)
