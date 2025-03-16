package com.chitas.carderio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "userDTO : id-"+id+" username-"+username+" email-"+email;
    }
}
