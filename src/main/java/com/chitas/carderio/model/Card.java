package com.chitas.carderio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String front;

    @Column(nullable = false)
    private String back;

    private Float interval = 0f;

    private LocalDateTime lastReviewDate;

    private Integer learningStep = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // One-to-Many with User

}
