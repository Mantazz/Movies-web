package org.app.DTO;

import jakarta.validation.constraints.*;

public record MovieDTO(
        @NotBlank @Size(max=50) String title,
        @Pattern(regexp = "^\\d{1,2}:[0-5][0-9]$", message = "length must be h:mm") String length,
        @Size(max=500) String notes,
        @Min(1) @Max(5) int rating
) {}
