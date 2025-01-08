package com.dionext.ai.ai_advanced.model.travel;

public record WeatherSummary(String description,
                             double temperature,
                             double minTemperature,
                             double maxTemperature,
                             int humidity,
                             String date) {
}
