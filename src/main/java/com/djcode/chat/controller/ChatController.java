package com.djcode.chat.controller;

import com.djcode.chat.service.ChatService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.DefaultChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {

    //    private final ChatClient chatClient;
//
//    public ChatController(ChatClient.Builder chatClientBuilder) {
//        this.chatClient = chatClientBuilder.build();
//    }
//
//    @GetMapping("/ai")
//    public String generation(String userPrompt){
//        return this.chatClient.prompt()
//                .user(userPrompt)
//                .call()
//                .content();
//    }
//
//    // 스트리밍 방식 -> Flux 사용
//    @GetMapping(value = "/st", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> stream(String userPrompt) {
//        return chatClient.prompt()
//                .user(userPrompt)
//                .stream()
//                .content();
//    }

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    public record PromptBody(@NotEmpty String conversationId,
                             @NotEmpty String userPrompt,
                             @Nullable String systemPrompt,
                             DefaultChatOptions chatOptions) {
    }

    @PostMapping(value = "/call", produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse call(@RequestBody @Valid PromptBody promptBody) {

        Prompt prompt = createPrompt(promptBody);
        return chatService.call(prompt, promptBody.conversationId);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody @Valid PromptBody promptBody) {
        Prompt prompt = createPrompt(promptBody);
        return chatService.stream(prompt, promptBody.conversationId());
    }

    private static Prompt createPrompt(PromptBody promptBody) {
        List<Message> messages = new ArrayList<>();

        if (promptBody.systemPrompt() != null && !promptBody.systemPrompt().isBlank()) {
            messages.add(new SystemMessage(promptBody.systemPrompt()));
        }

        messages.add(new UserMessage(promptBody.userPrompt()));
        Prompt.Builder promptBuilder = Prompt.builder().messages(messages);

        if (promptBody.chatOptions() != null) {
            promptBuilder.chatOptions(promptBody.chatOptions());
        }
        return promptBuilder.build();
    }
}
