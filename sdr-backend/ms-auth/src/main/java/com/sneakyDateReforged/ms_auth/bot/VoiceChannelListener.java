//package com.sneakyDateReforged.ms_auth.bot;
//
//import lombok.extern.slf4j.Slf4j;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import net.dv8tion.jda.api.hooks.EventListener;
//import okhttp3.*;
//
//import java.io.IOException;
//
//@Slf4j
//public class VoiceChannelListener extends ListenerAdapter implements EventListener {
//
//    private final OkHttpClient client = new OkHttpClient();
//    private static final String BACKEND_URL = "http://ms-auth:8082/api/auth/discord/sync";
//
//    @Override
//    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
//        System.out.println(">>> [DEBUG] onGuildVoiceUpdate déclenché");
//        log.info(">>> [DEBUG] Event déclenché avec member = {}", event.getMember());
//
//        // Si l’utilisateur vient de rejoindre un salon vocal (nouveau salon ≠ null)
//        if (event.getChannelJoined() != null) {
//            User user = event.getMember().getUser();
//
//            String discordId = user.getId();
//            String username = user.getName();
//            String avatarUrl = user.getAvatarUrl();
//
//            System.out.println(">>> [DEBUG] Utilisateur a rejoint un salon vocal : " + username);
//            System.out.println(">>> [DEBUG] discordId=" + discordId + ", avatarUrl=" + avatarUrl);
//
//            log.info("🔔 Connexion vocale détectée : {}", username);
//
//            String json = String.format("""
//                {
//                  "discordId": "%s",
//                  "discordUsername": "%s",
//                  "discordAvatarUrl": "%s"
//                }
//                """, discordId, username, avatarUrl);
//
//            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
//            Request request = new Request.Builder()
//                    .url(BACKEND_URL)
//                    .post(body)
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override public void onFailure(Call call, IOException e) {
//                    System.out.println(">>> [ERROR] Échec lors de l'envoi vers le backend : " + e.getMessage());
//                    log.error("❌ Erreur d'envoi Discord → Backend", e);
//                }
//
//                @Override public void onResponse(Call call, Response response) {
//                    System.out.println(">>> [DEBUG] Réponse backend : " + response.code());
//                    log.info("📨 Réponse backend : {}", response.code());
//                }
//            });
//        } else {
//            System.out.println(">>> [DEBUG] Utilisateur a quitté un salon vocal (aucun salon rejoint)");
//        }
//    }
//}
//
//package com.sneakyDateReforged.ms_auth.bot;
//
//import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
//import com.sneakyDateReforged.ms_auth.service.DiscordSyncService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.User;
//import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
//import net.dv8tion.jda.api.hooks.ListenerAdapter;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class VoiceChannelListener extends ListenerAdapter {
//
//    private final DiscordSyncService discordSyncService;
//
//    @Override
//    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
//        if (event.getChannelJoined() == null) {
//            // utilisateur a quitté / a été move hors vocal → on ignore
//            return;
//        }
//
//        final Member member = event.getMember();
//        final User user = member.getUser();
//
//        final String discriminator = user.getDiscriminator(); // peut être "0000" avec les nouveaux usernames
//        final String username = (discriminator != null && !discriminator.isBlank() && !"0000".equals(discriminator))
//                ? user.getName() + "#" + discriminator
//                : user.getName();
//
//        final String nickname = (member.getNickname() != null) ? member.getNickname() : user.getName();
//
//        log.info("🔔 Connexion vocale: userId={}, username={}, nick={}",
//                user.getId(), username, nickname);
//
//        // on met à jour le profil (pas de state ici)
//        DiscordSyncRequestDTO dto = DiscordSyncRequestDTO.builder()
//                .discordId(user.getId())
//                .discordUsername(username)
//                .discordDiscriminator(discriminator)
//                .discordNickname(nickname)
//                .discordAvatarUrl(user.getEffectiveAvatarUrl())
//                .build();
//
//        discordSyncService.handleSync(dto);
//        // pas d'appel à markConnectedFrom(dto) ici (on ne veut pas toucher à la checkbox sans state)
//    }
//}
package com.sneakyDateReforged.ms_auth.bot;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.service.DiscordSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceChannelListener extends ListenerAdapter {

    private final DiscordSyncService discordSyncService;

    // ⬅ Injecte l'ID du salon vocal d'auth depuis la config
    @Value("${discord.auth-voice-channel-id}")
    private String authVoiceChannelId;

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {

        // Si getChannelJoined() est null -> il a quitté / bougé ailleurs -> on s'en fout
        if (event.getChannelJoined() == null) {
            return;
        }

        // Salon vocal rejoint
        final var joinedChannel = event.getChannelJoined();
        final String joinedChannelId = joinedChannel.getId();

        // On ne s'intéresse qu'au salon vocal d'auth
        if (!joinedChannelId.equals(authVoiceChannelId)) {
            // Il a rejoint un autre salon vocal : on ne valide pas la checkbox
            return;
        }

        final Member member = event.getMember();
        final User user = member.getUser();

        // On ignore le bot lui-même
        if (user.isBot()) {
            return;
        }

        // Construit un username Discord "pseudo#1234" si le discriminant existe encore,
        // sinon juste le name. (tu l'avais déjà, je reprends pareil)
        final String discriminator = user.getDiscriminator(); // peut être "0000" avec les nouveaux usernames
        final String username = (discriminator != null && !discriminator.isBlank() && !"0000".equals(discriminator))
                ? user.getName() + "#" + discriminator
                : user.getName();

        final String nickname = (member.getNickname() != null) ? member.getNickname() : user.getName();
        final String chosenPseudo = (member.getNickname() != null)
                ? member.getNickname()
                : username;

        log.info(
                "🔔 Connexion vocale AUTH: userId={}, username={}, nick={}, chosenPseudo={}, channelId={}",
                user.getId(), username, nickname, chosenPseudo, joinedChannelId
        );

        // 1) Toujours : on met à jour le profil en base (comme avant)
        DiscordSyncRequestDTO dto = DiscordSyncRequestDTO.builder()
                .discordId(user.getId())
                .discordUsername(username)
                .discordDiscriminator(discriminator)
                .discordNickname(nickname)
                .discordAvatarUrl(user.getEffectiveAvatarUrl())
                .build();

        discordSyncService.handleSync(dto);

        // 2) Nouveau : on considère que cette arrivée dans le salon vocal est une preuve OK
        //    => on marque TOUS les states en attente comme "connectés"
        discordSyncService.markAllPendingAsConnectedFromVoiceJoin(user.getId(), chosenPseudo);

        // Avant, tu t'arrêtais ici sans rien faire pour cocher la checkbox.
        // Maintenant, le polling Angular sur /discord/status?state=xxx
        // va voir connected=true et donc débloquer le bouton "Créer mon compte".
    }
}
