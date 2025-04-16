package com.example.elderease.chatbot;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    private List<Content> contents;

    public GeminiRequest(String text) {
        this.contents = Collections.singletonList(new Content(text));
    }

    static class Content {
        private String role = "user";
        private List<Part> parts;

        public Content(String text) {
            this.parts = Collections.singletonList(new Part(text));
        }
    }

    static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
