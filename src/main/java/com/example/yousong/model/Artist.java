package com.example.yousong.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/* -------- Validierung auf der Server-Seite -------- */
@Entity
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Artist name is required!")
    @NotBlank(message = "Artist name cannot be blank")
    @Size(min = 2, max = 100, message = "Artist name must be between 2 and 100 characters")
    private String name;

    public Artist() {}

    public Artist(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
