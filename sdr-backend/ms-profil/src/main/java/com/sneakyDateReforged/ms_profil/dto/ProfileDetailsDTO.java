package com.sneakyDateReforged.ms_profil.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data @Builder
public class ProfileDetailsDTO {

    @Data @Builder
    public static class Identity {
        private String pseudo;
        private String avatarUrl;
    }

    @Data @Builder
    public static class Game {
        private String name;
        private Integer hours; // optionnel
    }

    private Identity discord;
    private Identity steam;
    private List<Game> games;
}
