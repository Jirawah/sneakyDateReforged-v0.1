package com.sneakyDateReforged.ms_profil.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProfileUpdateDTO {
    @Size(min = 2, max = 50)
    private String displayName;

    @Size(max = 1000)
    private String bio;

    private String country;
    private String languages;
    private Integer age;
}
