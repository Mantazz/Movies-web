package org.app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NameChangeRequestDTO(
    @NotBlank @Size(max = 30) String name
) {}
