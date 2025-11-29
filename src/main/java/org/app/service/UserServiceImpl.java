package org.app.service;

import org.app.model.AppUser;
import org.app.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUser saveUser(AppUser user) {
        return userRepository.save(user);
    }

    @Override
    public AppUser findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public AppUser changeName(String username, String newName) {

        if (newName == null || newName.trim().isEmpty()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        String  cleaned = newName.trim();
        if (cleaned.length() > 20) {
            throw new IllegalArgumentException("Name cannot exceed 20 characters");
        }

        AppUser user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        user.setName(cleaned);
        return userRepository.save(user);

    }

    @Override
    public Optional<AppUser> findUserById(Long id) {
        return userRepository.findById(id);
    }
}
