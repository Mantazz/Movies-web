package org.app.DTO;

import org.app.model.Role;

public record UserResponseDTO(
    Long id,
    String username,
    String name,
    String surname,
    Role role
) {}
