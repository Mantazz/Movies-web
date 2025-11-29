package org.app.service;

import org.app.DTO.AppUserDTO;
import org.app.DTO.AuthDTO;
import org.app.DTO.AuthResponseDTO;


public interface AuthService {
    AuthResponseDTO registerUser (AppUserDTO appUserDTO);
    AuthResponseDTO loginUser (AuthDTO authDTO);
}
