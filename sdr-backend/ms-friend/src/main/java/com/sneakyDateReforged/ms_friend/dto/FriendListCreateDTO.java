package com.sneakyDateReforged.ms_friend.dto;

import jakarta.validation.constraints.NotBlank;

public record FriendListCreateDTO(
        @NotBlank String name
) {}
