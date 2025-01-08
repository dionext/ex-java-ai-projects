package com.dionext.ai.ai_base.service;

import com.dionext.ai.ai_base.model.ActorFilms;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.model.Media;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AIService {
    @Autowired
    //private final OpenAiChatModel chatModel;
    private OllamaChatModel chatModel;

    private ChatClient chatClient;

    //public AIService(OpenAiChatModel chatModel) {
      //  this.chatModel = chatModel;
    public AIService() {
    }
    @PostConstruct
    void postConstruct(){
        ChatClient.Builder builder = ChatClient.builder(chatModel);
        //https://docs.spring.io/spring-ai/reference/api/chatclient.html
        chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor()).build();

    }
    public String simplePrompt(String message) {
        Prompt prompt = new Prompt(message);
        //return chatModel.call(prompt).getResult().getOutput().getContent();
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
    public String simplePromptWithAdvisor(String message, VectorStore vectorStore) {
        Prompt prompt = new Prompt(message);
        return chatClient.prompt()
                .advisors(new QuestionAnswerAdvisor(vectorStore,
                        SearchRequest.defaults()))
                .user(message).call().content();
    }
    public String promptWithTemplate(String category, String year) {
        String template = """
                 Please provide me best book for the given {category} and the {year}.
                 Please do provide a summary of the book as well, the information should be\s
                 limited and not much in depth. The response should be in the JSON format
                 containing this information:
                 category, book, year, review, author, summary
                """;
        PromptTemplate promptTemplate = new PromptTemplate(template);
        promptTemplate.add("category", category);
        promptTemplate.add("year", year);
        Prompt prompt = promptTemplate.create();
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    // summarize text in the structured format
    public String getSummarizeText(String text){
        String template = """
                 You will be given an {article}. 
                 You need to summarize it and provide the
                 output in the JSON format with these keys : 
                 topic, highlights.
                """;
        PromptTemplate promptTemplate = new PromptTemplate(template);
        promptTemplate.add("article", text);
        Prompt prompt = promptTemplate.create();
        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    // multimodal - read images
    //query = "Explain what do you see on this picture?"
    public String getImageChatReader(String query) throws IOException {
        Resource imageResource = new ClassPathResource("/sample.png");
        byte[] imageData = imageResource.getContentAsByteArray();

        UserMessage userMessage = new UserMessage(query,
                List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageResource)));

        return chatModel.call(new Prompt(userMessage)).getResult().getOutput().getContent();
    }
    public ActorFilms promptWithObjectResult(){
        ActorFilms actorFilms = chatClient.prompt()
                .user("Generate the filmography for a Tom Hanks actor.")
                .call()
                .entity(ActorFilms.class);
        /*
        List<ActorFilms> actorFilms = chatClient.prompt()
                .user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray.")
                .call()
                .entity(new ParameterizedTypeReference<List<ActorFilms>>() {
                });*/
        return actorFilms;
    }
    public List<ActorFilms> promptWithListOfObjectsResult(){

        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor(
                request -> "Custom request: " + request.userText(),
                response -> "Custom response: " + response.getResult(), 1
        );

        List<ActorFilms> actorFilms = chatClient.prompt()
                .user("Generate the filmography for a Tom Hanks actor.")
                //.advisors(new SimpleLoggerAdvisor())
                .advisors(new SimpleLoggerAdvisor(
                        request -> "Custom request: " + request.userText(),
                        response -> "Custom response: " + response.getResult(), 1
                ))
                .call()
                .entity(new ParameterizedTypeReference<List<ActorFilms>>() {
                });
        return actorFilms;
    }
    public List<ActorFilms> fluxPromptWithObjectResult(){
        var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<ActorFilms>>() { });

        Flux<String> flux = this.chatClient.prompt()
                .user(u -> u.text("""
                            Generate the filmography for a Tom Hanks actor.
                            {format}
                          """)
                        .param("format", converter.getFormat()))
                .stream()
                .content();

        String content = flux.collectList().block().stream().collect(Collectors.joining());

        List<ActorFilms> actorFilms = converter.convert(content);
        return actorFilms;
    }

}
