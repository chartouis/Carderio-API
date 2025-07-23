package com.chitas.carderio.service;

import com.chitas.carderio.model.Card;
import com.chitas.carderio.model.DTO.CardDTO;
import com.chitas.carderio.model.User;
import com.chitas.carderio.model.api.AIprompt;
import com.chitas.carderio.model.api.CardCheck;
import com.chitas.carderio.model.api.Progress;
import com.chitas.carderio.model.api.RequestDate;
import com.chitas.carderio.repo.CardsRepo;
import com.chitas.carderio.repo.UsersRepo;
import com.chitas.carderio.utils.AnnoyingConstants;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final UsersRepo usersRepo;
    private final CardsRepo cardsRepo;
    private final AIService aiService;
    private final AnnoyingConstants aconst;


    public CardService(UsersRepo usersRepo, CardsRepo cardsRepo, AIService aiService, AnnoyingConstants aconst){
        this.cardsRepo = cardsRepo;
        this.usersRepo = usersRepo;
        this.aiService = aiService;
        this.aconst = aconst;
    
    }

    public CardDTO cardToDto(Card card){
        Long folderId = (card.getFolder() == null) ? 0L : card.getFolder().getId();
        return new CardDTO(card.getId(), card.getBack(), card.getFront(), folderId);
    }

    public CardDTO cardIdToDto(Long id){
        if(cardsRepo.existsById(id)){
            System.out.println("A card with this id does not exist");
            return getDefaultCardDTO();}
        Card card = cardsRepo.findById(id).orElseThrow();
        Long folderId = (card.getFolder() == null) ? 0L : card.getFolder().getId();
        return new CardDTO(card.getId(), card.getBack(), card.getFront(), folderId);
    }

    public List<CardDTO> convertToDto(List<Card> cards) {
        return cards.stream()
                .map(this::cardToDto)  // Use the method for single Card to CardDTO conversion
                .collect(Collectors.toList());
    }

    public List<CardDTO> getUserCards() {
        return convertToDto(usersRepo.findCardsByUsername(aconst.getCurrentUsername()));
    }


    public CardDTO createCard(Card card) {
        card.setUser(aconst.getCurrentUser());
        cardsRepo.save(card);
        Long folderId = (card.getFolder() == null) ? 0L : card.getFolder().getId();
        return new CardDTO(card.getId(), card.getBack(), card.getFront(), folderId);
    }

    public boolean deleteById(Long id) {
        if(!cardsRepo.existsById(id)){
            System.out.println("A card with this id does not exist");
            return false;}
        String cardUsername = cardsRepo.findById(id).orElseThrow().getUser().getUsername();
        String senderUsername = aconst.getCurrentUsername();
        if (senderUsername.equals(cardUsername)){
            cardsRepo.deleteById(id);
            return true;
        } else{
            System.out.println("This is not your card");
            return false;
        }

    }
    public static CardDTO getDefaultCardDTO(){
        return new CardDTO(0L, "failed to get the card","",0L);
    }

    public List<CardDTO> createCards(List<Card> cards) {
        List<CardDTO> dtos = new ArrayList<>();
        for (Card card : cards){
            dtos.add(createCard(card));
        }
        return dtos;
    }

    public void evaluate(CardCheck cardCheck, String requestDate) {
        updateCard(cardCheck.getCardId(), cardCheck.getIsCorrect(), requestDate);
    }

    //Method that returns an appropriate card according to its date and other data
    public List<CardDTO> getStack(RequestDate requestDate) {
        ArrayList<CardDTO> cards = new ArrayList<>();
        LocalDateTime date = LocalDateTime.parse(requestDate.getLocalDateTime(), DateTimeFormatter.ISO_DATE_TIME);
        for(Card card: usersRepo.findCardsByUsername(aconst.getCurrentUsername())){
            if (isDue(card,date)){
                cards.add(cardToDto(card));
            }
        }
        return cards;
    }

    private static final double EASE_FACTOR = 3;
    private static final float[] RELEARNING_INTERVALS_MINUTES = {1, 10, 60}; 
    private static final float INITIAL_INTERVAL_DAYS = 0.007f; 

    public void updateCard(Long cardId, boolean isCorrect, String requestDate) {
        Card card = cardsRepo.findById(cardId).orElseThrow();
        Float currentInterval = card.getInterval();
        Integer learningStep = card.getLearningStep();
        LocalDateTime date = LocalDateTime.parse(requestDate, DateTimeFormatter.ISO_DATE_TIME);
        if (!isDue(card, date)) {
            return;
        }
        if (isCorrect) {
            if (learningStep > 0) {
                // Exit relearning phase
                card.setInterval(INITIAL_INTERVAL_DAYS);
                card.setLearningStep(0);
            } else {
                // Handle new cards (currentInterval = 0)
                float newInterval = (currentInterval == 0)
                        ? INITIAL_INTERVAL_DAYS
                        : (float) (currentInterval * EASE_FACTOR);
                card.setInterval(newInterval);
            }
        } else {
            if (learningStep == 0) {
                // Enter relearning phase
                card.setInterval(RELEARNING_INTERVALS_MINUTES[0]);
                card.setLearningStep(1);
            } else {
                // Progress through relearning steps
                int nextStep = Math.min(learningStep + 1, RELEARNING_INTERVALS_MINUTES.length);
                card.setInterval(RELEARNING_INTERVALS_MINUTES[nextStep - 1]);
                card.setLearningStep(nextStep);
            }
        }

        card.setLastReviewDate(date);

        cardsRepo.save(card);

    }

    public boolean isDue(
            Card card,
            LocalDateTime currentDateTime
    ) {
        LocalDateTime lastReviewDate = card.getLastReviewDate();
        float interval = card.getInterval();
        int learningStep = card.getLearningStep();
        if (lastReviewDate == null) {

            card.setLastReviewDate(currentDateTime);
            cardsRepo.save(card);
            return true;
        }

        LocalDateTime dueDate;

        if (learningStep == 0) {
            long minutes =Math.round((interval % 1) * 1440);
            int days = (int)interval;
            dueDate = lastReviewDate.plusDays(days).plusMinutes(minutes);

        } else {

            dueDate = lastReviewDate.plusMinutes((int)interval);
        }

        // Card is due if current time is >= due date
        return currentDateTime.isAfter(dueDate) || currentDateTime.equals(dueDate);
    }

    public Progress getProgress(RequestDate requestDate) {
        long learn = 0;
        long know = 0;
        LocalDateTime date = LocalDateTime.parse(requestDate.getLocalDateTime(), DateTimeFormatter.ISO_DATE_TIME);
        for(Card card : usersRepo.findCardsByUsername(aconst.getCurrentUsername())){
            if (isDue(card, date)){
                learn+=1;
            }else {
                know+=1;
            }
        }

        return new Progress(learn,know);
    }

    public CardDTO patchCard(CardDTO newCard) {
        User user = aconst.getCurrentUser();
        Card card = cardsRepo.findById(newCard.getId()).orElseThrow();
        if(!card.getUser().getId().equals(user.getId())){
            return getDefaultCardDTO();
        }
        card.setBack(newCard.getBack());
        card.setFront(newCard.getFront());
        cardsRepo.save(card);
        return newCard;
    }

    public List<CardDTO> generateAIStack(AIprompt prompt) {
        return aiService.generateAIStack(prompt);
    }


}
