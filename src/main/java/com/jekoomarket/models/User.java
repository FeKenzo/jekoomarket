package com.jekoomarket.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data                   // getters, setters, toString, equals e hashCode
@NoArgsConstructor      // Construtor vazio
@AllArgsConstructor     // Construtor com todos os atributos
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
