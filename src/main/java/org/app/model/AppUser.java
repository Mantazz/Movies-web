package org.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Column(unique = true) // username is unique to every user
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
