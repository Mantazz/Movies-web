package org.app.service;

import org.app.model.AppUser;

import java.util.Optional;

public interface UserService {
    AppUser saveUser(AppUser user);
    AppUser findUserByUsername(String username);
    AppUser changeName(String username, String newName);
    Optional<AppUser> findUserById(Long id);
}
