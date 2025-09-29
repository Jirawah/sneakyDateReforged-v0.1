package com.sneakyDateReforged.ms_friend.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequestDTO(
        @NotNull Long targetUserId
) {}
