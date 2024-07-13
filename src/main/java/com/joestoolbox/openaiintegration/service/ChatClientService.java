package com.joestoolbox.openaiintegration.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tech.conceptualarts.logginghelper.adapter.LogAdapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static tech.conceptualarts.logginghelper.enums.LogType.*;

@Service
@RequiredArgsConstructor
public class ChatClientService {

    @Value("classpath:/prompts/system-message.st")
    private Resource systemResource;
    private final Logger LOG = LoggerFactory.getLogger(ChatClientService.class);
    private final String CLASSNAME = ChatClientService.class.getName();
    private final LogAdapter logAdapter;
    private final ChatClient chatClient;

    public Optional<ChatResponse> generate(Prompt prompt) {
        String methodSignature = "generate(Prompt)";
        logAdapter.logToFile(LOG, CLASSNAME, methodSignature, ENTER);
        try {
            ChatResponse generatedResult = this.chatClient.prompt(prompt).call().chatResponse();
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXIT);
            return Optional.of(generatedResult);
        } catch (Exception e) {
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXCEPTION, e.getMessage());
            logAdapter.logToExternal(e);
            return Optional.empty();
        }
    }

    public Optional<Prompt> createPrompt(Message userMessage, Message systemMessage) {
        String methodSignature = "createPrompt(Message, Message)";
        logAdapter.logToFile(LOG, CLASSNAME, methodSignature, ENTER);
        try {
            Prompt prompt = new Prompt(List.of(userMessage, systemMessage));
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXIT);
            return Optional.of(prompt);
        } catch (Exception e) {
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXCEPTION, e.getMessage());
            logAdapter.logToExternal(e);
            return Optional.empty();
        }
    }

    public Optional<Message> getUserMessage(String userInput) {
        String methodSignature = "getUserMessage(String)";
        logAdapter.logToFile(LOG, CLASSNAME, methodSignature, ENTER);
        try {
            Message userMessage = new UserMessage(userInput);
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXIT);
            return Optional.of(userMessage);
        } catch (Exception e) {
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXCEPTION, e.getMessage());
            logAdapter.logToExternal(e);
            return Optional.empty();
        }
    }

    public Optional<Message> getSystemMessage(Map<String, Object> promptValues) {
        String methodSignature = "getSystemMessage(Map<String, Object>)";
        logAdapter.logToFile(LOG, CLASSNAME, methodSignature, ENTER);
        try {
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemResource);
            Message systemMessage = systemPromptTemplate.createMessage(promptValues);
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXIT);
            return Optional.of(systemMessage);
        } catch (Exception e) {
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXCEPTION, e.getMessage());
            logAdapter.logToExternal(e);
            return Optional.empty();
        }
    }

}
