//package com.sneakyDateReforged.ms_auth.config;
//
//import com.sneakyDateReforged.ms_auth.bot.DiscordBot;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class DiscordBotConfig implements CommandLineRunner {
//
//    private final DiscordBot discordBot;
//
//    @Override
//    public void run(String... args) {
//        discordBot.start();
//    }
//}
package com.sneakyDateReforged.ms_auth.config;

import com.sneakyDateReforged.ms_auth.bot.LinkCommandListener;
import com.sneakyDateReforged.ms_auth.bot.VoiceChannelListener; // ✅ AJOUT
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DiscordBotConfig {

    @Value("${discord.token}")
    private String discordToken;

    // optionnel mais recommandé pour un enregistrement instantané de la commande
    @Value("${discord.guild-id:}")
    private String guildId;

    private final LinkCommandListener linkCommandListener;
    private final VoiceChannelListener voiceChannelListener; // ✅ AJOUT

    @Bean
    public JDA jda() throws Exception {
        JDA jda = JDABuilder.createDefault(discordToken)
                .enableIntents(
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT // retire-le si tu n’écoutes pas le contenu des messages
                )
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                // ✅ on enregistre les deux listeners
                .addEventListeners(
                        linkCommandListener,
                        voiceChannelListener
                )
                .build();

        jda.awaitReady();
        log.info("🤖 JDA prêt. Bot: {}", jda.getSelfUser().getName());

        // Enregistre la commande /link
        if (guildId != null && !guildId.isBlank()) {
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
                guild.updateCommands()
                        .addCommands(
                                Commands.slash("link", "Lier ton compte au site avec le code affiché")
                                        .addOption(OptionType.STRING, "code", "Code affiché sur le site", true)
                        ).queue(
                                ok -> log.info("✅ Slash command /link enregistrée dans le guild {}", guild.getName()),
                                err -> log.warn("❌ Échec enregistrement /link pour guild {}: {}", guildId, err.getMessage())
                        );
            } else {
                log.warn("Guild {} introuvable, enregistrement global de /link", guildId);
                jda.updateCommands()
                        .addCommands(
                                Commands.slash("link", "Lier ton compte au site")
                                        .addOption(OptionType.STRING, "code", "Code affiché sur le site", true)
                        ).queue();
            }
        } else {
            // Global (propagation lente)
            jda.updateCommands()
                    .addCommands(
                            Commands.slash("link", "Lier ton compte au site")
                                    .addOption(OptionType.STRING, "code", "Code affiché sur le site", true)
                    ).queue();
        }

        return jda;
    }
}

