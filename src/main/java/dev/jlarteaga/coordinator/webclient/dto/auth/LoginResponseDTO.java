package dev.jlarteaga.coordinator.webclient.dto.auth;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String email;
    private String uuid;
    private String token;
    private Integer expiresIn;
}
