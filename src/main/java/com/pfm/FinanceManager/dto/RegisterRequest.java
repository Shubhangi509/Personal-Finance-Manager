package com.pfm.FinanceManager.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String username;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "\\+?[0-9]{10,15}")
    private String phoneNumber;
}
