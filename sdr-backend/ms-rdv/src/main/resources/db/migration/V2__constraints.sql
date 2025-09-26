-- Empêche les doublons d'un même user sur un même rdv
CREATE UNIQUE INDEX idx_participant_unique_user ON participant(rdv_id, user_id);

-- (optionnel) index sur statut de participation
CREATE INDEX idx_participant_status ON participant(rdv_id, statut_participation);
