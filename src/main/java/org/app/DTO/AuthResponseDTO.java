package org.app.DTO;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String message;

    public AuthResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
    }
}
