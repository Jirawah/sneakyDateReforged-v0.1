package com.sneakyDateReforged.ms_auth.bot;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.service.DiscordSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LinkCommandListener extends ListenerAdapter {

    private final DiscordSyncService discordSyncService;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!"link".equals(event.getName())) return;

        final String state = event.getOption("code") != null ? event.getOption("code").getAsString() : null;
        if (state == null || state.isBlank()) {
            event.reply("❌ Code manquant. Utilise `/link <code>` affiché sur le site.")
                    .setEphemeral(true).queue();
            return;
        }

        final User user = event.getUser();
        final Member member = event.getMember();
        final String discriminator = user.getDiscriminator(); // peut être "0000" avec la nouvelle nomenclature Discord
        final String username = discriminator != null && !discriminator.isBlank() && !"0000".equals(discriminator)
                ? user.getName() + "#" + discriminator
                : user.getName();

        final String nickname = (member != null && member.getNickname() != null)
                ? member.getNickname()
                : user.getName();

        final DiscordSyncRequestDTO dto = DiscordSyncRequestDTO.builder()
                .discordId(user.getId())
                .discordUsername(username)
                .discordDiscriminator(discriminator)
                .discordNickname(nickname)
                .discordAvatarUrl(user.getEffectiveAvatarUrl())
                .state(state.trim())
                .build();

        // ↴ même logique que /sync : maj profil + marquer "connected"
        discordSyncService.handleSync(dto);
        discordSyncService.markConnectedFrom(dto);

        event.reply("✅ Compte lié, tu peux revenir sur le site et terminer l’inscription.")
                .setEphemeral(true).queue();
    }
}
