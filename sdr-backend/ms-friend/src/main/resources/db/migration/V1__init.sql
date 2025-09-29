-- USERS: référencés depuis ms-auth, ici on stocke seulement les IDs (foreign keys logiques)

-- Table des relations d'amitié (symétriques)
CREATE TABLE IF NOT EXISTS friend (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'PENDING', -- PENDING | ACCEPTED | BLOCKED
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  -- Colonnes générées pour imposer l'unicité bidirectionnelle
  u_min BIGINT AS (LEAST(user_id, friend_id)) STORED,
  u_max BIGINT AS (GREATEST(user_id, friend_id)) STORED
);

-- Unicité bidirectionnelle : (A,B) == (B,A)
CREATE UNIQUE INDEX uq_friend_sym ON friend (u_min, u_max);

-- Accélérer recherches courantes
CREATE INDEX idx_friend_user ON friend (user_id, status);
CREATE INDEX idx_friend_friend ON friend (friend_id, status);

-- Listes d'amis (groupes)
CREATE TABLE IF NOT EXISTS friend_list (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Un utilisateur ne doit pas avoir deux listes avec le même nom
CREATE UNIQUE INDEX uq_friend_list_owner_name ON friend_list (user_id, name);

-- Membres d'une liste
CREATE TABLE IF NOT EXISTS friend_list_member (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  friend_list_id BIGINT NOT NULL,
  member_user_id BIGINT NOT NULL,
  added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Unicité d'un membre dans une liste donnée
CREATE UNIQUE INDEX uq_friend_list_member ON friend_list_member (friend_list_id, member_user_id);

-- (Optionnel) FK logiques si tu veux : pas de contraintes physiques inter-MS
-- mais on peut protéger l'intégrité locale :
CREATE INDEX idx_flm_list ON friend_list_member (friend_list_id);
