package com.chitas.carderio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.chitas.carderio.model.Card;
import com.chitas.carderio.model.Folder;
import com.chitas.carderio.model.DTO.CardDTO;
import com.chitas.carderio.model.DTO.FolderDTO;
import com.chitas.carderio.model.api.RequestDate;
import com.chitas.carderio.service.CardService;
import com.chitas.carderio.service.FolderService;
import java.util.List;

@RestController
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;
    private final CardService cardService;

    public FolderController(FolderService folderService, CardService cardService) {
        this.folderService = folderService;
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<String> createFolder(@RequestBody Folder folder) {
        Folder created = folderService.createFolder(folder);
        if (created == null) {
            return ResponseEntity.badRequest().body("Failed to create folder");
        }
        return ResponseEntity.ok("Folder created with ID: " + created.getId());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateFolder(@PathVariable Long id, @RequestBody String name) {
        Folder updated = folderService.updateFolder(id, name);
        if (updated == null) {
            return ResponseEntity.badRequest().body("Failed to update folder");
        }
        return ResponseEntity.ok("Folder updated with ID: " + updated.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        if (folderService.deleteFolder(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<CardDTO>> getCardsInFolder(@PathVariable Long id) {
        List<Card> cards = folderService.getCardsInFolder(id);
        if (cards.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cardService.convertToDto(cards));
    }

    @PostMapping("/{id}/cards")
    public ResponseEntity<List<CardDTO>> getStackInFolder(@PathVariable Long id, @RequestBody RequestDate requestDate) {
        List<CardDTO> cards = folderService.getStackInFolder(id, requestDate);
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/{folderId}/cards/{cardId}")
    public ResponseEntity<Void> addCardToFolder(@PathVariable Long folderId, @PathVariable Long cardId) {
        if (folderService.addCardToFolder(cardId, folderId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<FolderDTO>> getAllUserFolders() {
        List<FolderDTO> folders = folderService.getAllUserFolders();
        return ResponseEntity.ok(folders);
    }
}