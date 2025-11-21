package com.example.tuttifruttia.domain.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Groq implements LLMProvider{

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final MediaType JSON_MEDIA_TYPE =
            MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final String apiKey;
    private final String modelName;

    public Groq(String apiKey) {
        this(apiKey, "llama-3.3-70b-versatile");
    }


    public Groq(String apiKey, String modelName) {
        this.client = new OkHttpClient();
        this.gson = new Gson();
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey must not be null");
        this.modelName = Objects.requireNonNull(modelName, "modelName must not be null");
    }

    /**
     * Helper para crearlo leyendo la API key desde la variable de entorno GROQ_API_KEY.
     */
    public static Groq fromEnv() {

        String key = System.getenv("GROQ_API_KEY");

        System.out.println("DEBUG >>> GROQ_API_KEY = " + key);

       if (key == null || key.isBlank()) {
           throw new IllegalStateException(
                    "La variable de entorno GROQ_API_KEY no está definida."
           );
        }
       return new Groq(key);
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        JsonObject json = new JsonObject();
        json.addProperty("model", modelName);

        JsonArray messages = new JsonArray();

        if (systemPrompt != null && !systemPrompt.isBlank()) {
            JsonObject systemMessage = new JsonObject();
            systemMessage.addProperty("role", "system");
            systemMessage.addProperty("content", systemPrompt);
            messages.add(systemMessage);
        }

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);
        messages.add(userMessage);

        json.add("messages", messages);
        json.addProperty("temperature", 0.2); // más determinista

        RequestBody body = RequestBody.create(
                gson.toJson(json),
                JSON_MEDIA_TYPE
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null
                        ? new String(response.body().bytes(), StandardCharsets.UTF_8)
                        : "sin cuerpo de error";
                throw new IOException("Error HTTP " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body() != null
                    ? new String(response.body().bytes(), StandardCharsets.UTF_8)
                    : "";

            JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
            JsonArray choices = responseJson.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new IOException("Respuesta de Groq sin choices: " + responseBody);
            }

            JsonObject firstChoice = choices.get(0).getAsJsonObject();
            JsonObject messageObject = firstChoice.getAsJsonObject("message");
            return messageObject.get("content").getAsString();
        } catch (IOException e) {
            throw new RuntimeException("Error llamando a Groq", e);
        }
    }

}
