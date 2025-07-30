package com.sneakyDateReforged.ms_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank
    private String pseudo;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String steamId;

    @NotBlank
    @Size(min = 12)
    private String password;

    @NotBlank
    private String confirmPassword;

    public String getDiscordId;

    public String getDiscordId() {
        return "";
    }
}
