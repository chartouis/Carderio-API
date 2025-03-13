package com.chitas.carderio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardDTO {
    public CardDTO(String back, String front) {
        this.back = back;
        this.front = front;
    }

    public CardDTO() {
        // default constructor
    }

    private Long id;
    private String back;
    private String front;
}
