CREATE DATABASE IF NOT EXISTS authdb;

CREATE USER IF NOT EXISTS 'msauthuser'@'%' IDENTIFIED BY 'msauthpwd';
GRANT ALL PRIVILEGES ON authdb.* TO 'msauthuser'@'%';
FLUSH PRIVILEGES;

USE authdb;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_register_user $$

CREATE PROCEDURE sp_register_user (
    IN  p_email                    VARCHAR(255),
    IN  p_pseudo                   VARCHAR(255),
    IN  p_password                 VARCHAR(255),
    IN  p_steam_id                 VARCHAR(255),

    -- Discord complet
    IN  p_discord_id               VARCHAR(255),
    IN  p_discord_username         VARCHAR(255),
    IN  p_discord_discriminator    VARCHAR(32),
    IN  p_discord_nickname         VARCHAR(255),
    IN  p_discord_avatar_url       VARCHAR(512),
    IN  p_discord_validated        TINYINT(1),

    -- Steam "cosmétique" (ce qu'on affiche sur le profil)
    IN  p_steam_pseudo             VARCHAR(255),
    IN  p_steam_avatar             VARCHAR(512),

    OUT p_result_code              INT
)
BEGIN
    DECLARE existing_count INT DEFAULT 0;

    -- 1. Vérifier si email OU pseudo OU steam_id déjà utilisés
    SELECT COUNT(*) INTO existing_count
    FROM user_auth_model
    WHERE email = p_email
       OR pseudo = p_pseudo
       OR steam_id = p_steam_id;

    IF existing_count > 0 THEN
        SET p_result_code = -1;
    ELSE
        -- 2. Insérer l'utilisateur complet
        INSERT INTO user_auth_model (
            email,
            pseudo,
            password,
            steam_id,
            steam_pseudo,
            steam_avatar,
            steam_validated,

            discord_id,
            discord_username,
            discord_discriminator,
            discord_nickname,
            discord_avatar_url,
            discord_validated,

            role,
            created_at,
            updated_at
        ) VALUES (
            p_email,
            p_pseudo,
            p_password,
            p_steam_id,
            p_steam_pseudo,
            p_steam_avatar,
            0, -- steam_validated par défaut = 0

            p_discord_id,
            p_discord_username,
            p_discord_discriminator,
            p_discord_nickname,
            p_discord_avatar_url,
            p_discord_validated,

            'USER',
            NOW(),
            NOW()
        );

        SET p_result_code = 1;
    END IF;
END $$

DELIMITER ;

