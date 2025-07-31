package com.sneakyDateReforged.ms_auth.config;

import com.sneakyDateReforged.ms_auth.bot.DiscordBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DiscordBotConfig implements CommandLineRunner {

    private final DiscordBot discordBot;

    @Override
    public void run(String... args) {
        discordBot.start();
    }
}
