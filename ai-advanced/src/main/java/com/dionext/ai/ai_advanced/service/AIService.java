package com.dionext.ai.ai_advanced.service;

import com.dionext.ai.ai_advanced.model.travel.PlaceRecommendation;
import com.dionext.ai.ai_advanced.model.travel.PlaceSummary;
import com.dionext.ai.ai_advanced.model.travel.TravelItineraryItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {
    private final OpenAiChatModel chatModel;
    //private final OllamaChatModel chatModel;

    private ChatClient chatClient;

    public AIService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        //https://docs.spring.io/spring-ai/reference/api/chatclient.html
        chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();
    }


    public List<TravelItineraryItem> getTripRecommendation(List<PlaceRecommendation> placeRecommendations, String startDate,
                                                           String endDate) throws JsonProcessingException {
        String systemPromptTemplate = """
                You are a helpful AI travel planner assistant.
                Read the json data from ``top tourist places ``` first
                Then create a travel itinerary for visiting places in that based on rating, weather and reviews from that.
                Answer strictly based on the data above, dont assume anything else
                """;
    String userPromptTemplate = """
            ```top tourist places``` : ```{placeSummaryJson}```
            Create itinerary strictly for dates between ```{startDate}``` and ```{endDate}```.
            You should cover all the places in the list above.
            Place should not be repeated in the itinerary.
            ```reasonForChoosing``` should state why you have chosen that place. Include things like reviews, weather etc.
            Based on reviews and your knowledge about the place populate ```activity``` field in the output.
            Limit ```activity``` to 120 characters.
            Limit ```reasonForChoosing``` to 120 characters.
            In the output, the ```address``` field should match the ```address``` field in the input.
            ```time``` should be populated based on the time taken to visit the previous place and time to reach second one.
            Your answer should only be based on the data in input. Dont assume anything.
            """;

        List<PlaceSummary> placeSummaryList = placeRecommendations.stream().map(p ->
                PlaceSummary.placeRecommendationToPlaceSummary(p)).toList();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        String placeSummaryJson = objectWriter.writeValueAsString(placeSummaryList);
        Map<String,Object> variablesMap = Map.of(
                "placeSummaryJson", placeSummaryJson,
                "startDate", startDate,
                "endDate", endDate);
        return this.chatClient.prompt()
                .system(systemPromptTemplate)
                .user(u -> u.text(userPromptTemplate).params(variablesMap))
                .call()
                .entity(new ParameterizedTypeReference<List<TravelItineraryItem>>() {
                });

    }

}
