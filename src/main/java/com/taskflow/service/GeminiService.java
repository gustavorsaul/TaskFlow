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

        Client client = Client.builder()
                        .apiKey(apiKey)
                        .build();

        GenerateContentConfig config = 
            GenerateContentConfig.builder()
                .maxOutputTokens(100)
                .build();
                

        GenerateContentResponse response = 
            client.models.generateContent(
                "gemini-2.5-flash", 
                prompt, 
                config);
                
        return response.text();
    }

}
