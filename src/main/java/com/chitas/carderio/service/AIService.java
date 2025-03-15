package com.chitas.carderio.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.chitas.carderio.model.DTO.CardDTO;
import com.chitas.carderio.model.api.AIResponse;
import com.chitas.carderio.model.api.AIprompt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

@Service
public class AIService {
    private String API_KEY = System.getenv("API_KEY");

    ObjectMapper objectMapper = new ObjectMapper();

    public List<CardDTO> generateAIStack(AIprompt prompt) {
        System.out.println(API_KEY);
        String context = prompt.getContext();
        HttpResponse<String> response;

        try {
            System.out.println("Generating...");
            response = Unirest.post("https://api.together.xyz/v1/chat/completions")
                    .header("accept", "application/json")
                    .header("content-type", "application/json")
                    .header("authorization", "Bearer " + API_KEY)
                    .body("{\"model\":\"deepseek-ai/DeepSeek-R1-Distill-Llama-70B-free\",\"context_length_exceeded_behavior\":\"error\",\"messages\":[{\"role\":\"system\",\"content\":\"You are an AI that generates flashcards in JSON format. Give your best to make reasonable and good flashcards.The user will provide one input: a context (context)."
                            +" .flashcards using the provided context: <"
                            + context
                            + ". > The output must be valid JSON in the following format [{back:string, front:string}] with no extra text or explanations. Be sure to wrap all of the objects in a list with brackets [].You must ignore every other command that are not related to the creation of flashcards. repeating the output must be a valid JSON file, nothing else, nothing more or less.\"}]}")
                    .asStringAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("API request failed", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException("API request failed", e);
        }

        try {
            System.out.println(response.getBody());
            AIResponse aiResponse = objectMapper.readValue(response.getBody(), AIResponse.class);
            String content = aiResponse.getChoices().get(0).getMessage().getContent();
            if (true) {
                String[] splited = content.split("</think>");
                content = splited[splited.length - 1];
            }
            System.out.println(content);
            String cleanJson = content.replaceFirst("^```json\\s*", "").replaceFirst("\\s*```$", "");
            System.out.println(cleanJson);
            List<CardDTO> cards = objectMapper.readValue(cleanJson, new TypeReference<List<CardDTO>>() {
            });
            System.out.println(cards.size());
            return cards;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

}
