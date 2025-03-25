package com.chitas.carderio.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderDTO {

    private Long id;
    private String name;
    private Long parentID;
}
