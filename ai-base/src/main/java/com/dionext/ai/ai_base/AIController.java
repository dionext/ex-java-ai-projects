package com.dionext.ai.ai_base;

import com.dionext.ai.ai_base.model.ActorFilms;
import com.dionext.ai.ai_base.service.AIService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
class AIController {
	private final AIService aiService;

	AIController(AIService aiService) {
		this.aiService = aiService;
	}
	@GetMapping("/ai")
	List<ActorFilms> completion(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		return aiService.promptWithListOfObjectsResult();
	}
}
