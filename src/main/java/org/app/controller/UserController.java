package org.app.controller;

import org.app.DTO.NameChangeRequestDTO;
import org.app.DTO.UserResponseDTO;
import org.app.model.AppUser;
import org.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/name")
    public UserResponseDTO changeName(@RequestBody NameChangeRequestDTO request,
                                      Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findUserByUsername(username);
        AppUser updated = userService.changeName(username, request.name());
        return new UserResponseDTO(updated.getId(),
                                updated.getName(),
                                updated.getSurname(),
                                updated.getUsername(),
                                updated.getRole());
    }


}
