package com.joestoolbox.openaiintegration.adapter;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import tech.conceptualarts.logginghelper.adapter.LogAdapter;
import com.joestoolbox.openaiintegration.service.ChatClientService;

import java.util.Map;
import java.util.Optional;

import static tech.conceptualarts.logginghelper.enums.LogType.*;

@Component
@RequiredArgsConstructor
public class ChatClientAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ChatClientAdapter.class);
    private final String CLASSNAME = ChatClientAdapter.class.getName();
    private final LogAdapter logAdapter;
    private final ChatClientService chatClientService;


    public Optional<Map<String, Object>> generate(String userInput, Map<String, Object> systemMessageValues) {
        String methodSignature = "generate(String, Map<String, Object>)";
        logAdapter.logToFile(LOG, CLASSNAME, methodSignature, ENTER);
        try {

            if (StringUtils.isBlank(userInput)) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "User Input Cannot Be Null, Blank, Or Only Whitespace");
                return Optional.empty();
            } else if (systemMessageValues == null) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "System Message Values Cannot Be Null");
                return Optional.empty();
            } else if (systemMessageValues.isEmpty()) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "System Message Values Cannot Be Empty");
                return Optional.empty();
            }

            Optional<Message> maybeSystemMessage = chatClientService.getSystemMessage(systemMessageValues);
            Optional<Message> maybeUserMessage = chatClientService.getUserMessage(userInput);

            if (maybeSystemMessage.isEmpty()) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "System Message Not Found");
                return Optional.empty();
            }

            if (maybeUserMessage.isEmpty()) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "User Message Not Found");
                return Optional.empty();
            }

            Message systemMessage = maybeSystemMessage.get();
            Message userMessage = maybeUserMessage.get();

            Optional<Prompt> maybePrompt = chatClientService.createPrompt(userMessage, systemMessage);
            if (maybePrompt.isEmpty()) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "Prompt Not Found");
                return Optional.empty();
            }

            Prompt prompt = maybePrompt.get();

            Optional<ChatResponse> chatResponse = chatClientService.generate(prompt);
            if (chatResponse.isEmpty()) {
                logAdapter.logToFile(LOG, CLASSNAME, methodSignature, FAILED, "Chat Response Not Found");
                return Optional.empty();
            }

            ChatResponse response = chatResponse.get();
            Generation generation = response.getResult();
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXIT);
            return Optional.of(Map.of("generation", generation));
        } catch (Exception e) {
            logAdapter.logToFile(LOG, CLASSNAME, methodSignature, EXCEPTION, e.getMessage());
            logAdapter.logToExternal(e);
            return Optional.empty();
        }
    }
}
