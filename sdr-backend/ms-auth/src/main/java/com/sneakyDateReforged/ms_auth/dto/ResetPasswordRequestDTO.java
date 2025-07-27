package com.sneakyDateReforged.ms_auth.dto;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String token;
    private String newPassword;
}
