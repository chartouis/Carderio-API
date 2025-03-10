package com.chitas.carderio.controller;

import com.chitas.carderio.model.Card;
import com.chitas.carderio.model.DTO.CardDTO;
import com.chitas.carderio.model.api.CardCheck;
import com.chitas.carderio.model.api.Progress;
import com.chitas.carderio.model.api.RequestDate;
import com.chitas.carderio.repo.CardsRepo;
import com.chitas.carderio.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardsRepo cardsRepository, CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public CardDTO createCard(@RequestBody Card card) {
        return cardService.createCard(card);
    }

    @PostMapping("/m")
    public List<CardDTO> createCards(@RequestBody List<Card> cards) {
        return cardService.createCards(cards);
    }

    @GetMapping
    public List<CardDTO> getAllCards() {
        return cardService.getUserCards();
    }

    @GetMapping("/{id}")
    public CardDTO getCardById(@PathVariable Long id) {
        return cardService.cardIdToDto(id);
    }

    // @Transactional
    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteById(id);
    }

    @PatchMapping("/request")
    public CardCheck evaluate(@RequestBody CardCheck cardCheck) {
        cardService.evaluate(cardCheck, cardCheck.getLocalDateTime());
        return cardCheck;
    }

    @PostMapping("/request")
    public List<CardDTO> getStack(@RequestBody RequestDate requestDate) {
        return cardService.getStack(requestDate);
    }
    // Used to get the cards to actually show and open
    // @Deprecated
    // //@PostMapping("/request")
    // public CardDTO getFCard(@RequestBody RequestDate requestDate){
    // return cardService.getFCard(requestDate.getLocalDateTime());
    // }

    @PostMapping("/request/progress")
    public Progress getProgress(@RequestBody RequestDate requestDate) {
        return cardService.getProgress(requestDate);
    }

    @PatchMapping
    public CardDTO changeCard(@RequestBody CardDTO newCard) {
        return cardService.patchCard(newCard);
    }

}
