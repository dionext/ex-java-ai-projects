package com.dionext.ai.simple_ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
class AIController {
	private final ChatClient chatClient;

	private final EmbeddingModel embeddingModel;
	AIController(ChatClient chatClient, EmbeddingModel embeddingModel)
	{
		this.chatClient = chatClient;
		this.embeddingModel = embeddingModel;
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
	@GetMapping("/ai/embedding")
	public Map embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
		return Map.of("embedding", embeddingResponse);
	}
}
