//package com.sneakyDateReforged.ms_auth.bot;
//
//import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;
//import net.dv8tion.jda.api.JDA;
//import net.dv8tion.jda.api.JDABuilder;
//import net.dv8tion.jda.api.requests.GatewayIntent;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class DiscordBot {
//
//    private JDA jda;
//
//    @Value("${discord.token}")
//    private String discordToken;
//
//    public void start() {
//        try {
//            log.info("🚀 Initialisation du bot Discord...");
//
//            jda = JDABuilder.createLight(discordToken,
//                            GatewayIntent.GUILD_VOICE_STATES,
//                            GatewayIntent.GUILD_MEMBERS)
//                    .addEventListeners(new VoiceChannelListener())
//                    .build()
//                    .awaitReady();
//
//            log.info("✅ Bot Discord démarré avec succès.");
//        } catch (Exception e) {
//            log.error("❌ Erreur lors du démarrage du bot Discord :", e);
//        }
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        if (jda != null) {
//            log.info("🛑 Arrêt du bot Discord...");
//            jda.shutdown();
//        }
//    }
//}
