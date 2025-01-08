package com.dionext.ai.multy_model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class AIController {
	private final OpenAiChatModel chatModel;
	//private final OllamaChatModel chatModel;
	private ChatClient chatClient;

	public AIController(OpenAiChatModel chatModel) {
		this.chatModel = chatModel;
		ChatClient.Builder builder = ChatClient.builder(chatModel);
		//https://docs.spring.io/spring-ai/reference/api/chatclient.html
		chatClient = builder
				.defaultAdvisors(new SimpleLoggerAdvisor()).build();
	}

	@GetMapping("/ai")
	Map<String, String> completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		return Map.of(
				"completion",
				chatClient.prompt()
						.user(message)
						.call()
						.content());
	}
}
