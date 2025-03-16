package com.chitas.carderio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTO {
    public CardDTO(String back, String front) {
        this.back = back;
        this.front = front;
    }

    private Long id;
    private String back;
    private String front;


    @Override
    public String toString() {
        return "cardDTO : id-"+id+" back-"+back+" front-"+front;
    }
}
