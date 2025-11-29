package org.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String length;
    String notes;
    int rating;

    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY - user data is loaded only when needed
    @JoinColumn(name = "user_id")
    @JsonIgnore // Prevents serialization of the user field to avoid circular reference during JSON conversion
    private AppUser user;
}
