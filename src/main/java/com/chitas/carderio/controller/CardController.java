package com.chitas.carderio.controller;

import com.chitas.carderio.model.Card;
import com.chitas.carderio.model.DTO.CardDTO;
import com.chitas.carderio.model.api.AIprompt;
import com.chitas.carderio.model.api.CardCheck;
import com.chitas.carderio.model.api.Progress;
import com.chitas.carderio.model.api.RequestDate;
import com.chitas.carderio.service.CardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
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

    @PostMapping("/m/ai")
    public List<CardDTO> postMethodName(@RequestBody AIprompt prompt) {
        // TODO: this will receive a prompt in string which will be sended to an ai
        return cardService.generateAIStack(prompt);
        // return List.of(new CardDTO("ass1", "ass"), new CardDTO("ass2", "ass"), new
        // CardDTO("ass3", "ass"),
        // new CardDTO("ass4", "ass"), new CardDTO("ass5", "ass"), new CardDTO("ass6",
        // "ass")); //test function
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
    public boolean deleteCard(@PathVariable Long id) {
        return cardService.deleteById(id);

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
