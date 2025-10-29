package com.taskflow.service;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {

    public String askGemini(String prompt, String apiKey) {

        try {
            Client client = Client.builder()
                            .apiKey(apiKey)
                            .build();

            GenerateContentConfig config = 
                GenerateContentConfig.builder()
                    .temperature(0.4f)
                    .topP(0.8f)
                    .build();


            GenerateContentResponse response = 
                client.models.generateContent(
                    "gemini-2.5-flash", 
                    prompt, 
                    null);

            return response.text();
        } catch (Exception e) {
            return "Erro ao gerar a resposta: " + e.getMessage();
        }
    }

}
