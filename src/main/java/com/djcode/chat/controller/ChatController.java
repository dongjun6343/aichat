package com.djcode.chat.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;


    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ai")
    public String generation(String userPrompt){
        return this.chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    // 스트리밍 방식 -> Flux 사용
    @GetMapping(value = "/st", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(String userPrompt) {
        return chatClient.prompt()
                .user(userPrompt)
                .stream()
                .content();
    }
}
