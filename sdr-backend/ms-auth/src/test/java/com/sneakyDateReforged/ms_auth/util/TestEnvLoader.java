package com.sneakyDateReforged.ms_auth.util;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Charge les variables d'environnement depuis le fichier .env pour les tests.
 */
public class TestEnvLoader {

    private static boolean loaded = false;

    /**
     * Charge les variables du fichier .env une seule fois.
     */
    public static void loadEnv() {
        if (loaded) return; // éviter de recharger plusieurs fois

        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .ignoreIfMissing()
                .load();

        // Définir les variables comme propriétés système pour Spring/config
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

        System.out.println("[TestEnvLoader] Variables d'environnement chargées depuis .env");
        loaded = true;
    }

    /**
     * Récupère une variable d'environnement depuis les propriétés système.
     *
     * @param key           la clé de la variable
     * @param defaultValue  la valeur par défaut si non trouvée
     * @return              la valeur de la variable ou la valeur par défaut
     */
    public static String get(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
}
