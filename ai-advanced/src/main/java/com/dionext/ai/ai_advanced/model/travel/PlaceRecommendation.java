package com.dionext.ai.ai_advanced.model.travel;

import java.util.List;

public record PlaceRecommendation(PlaceRecords.Place place,
                                  List<String> photos,List<WeatherRecords.WeatherData> weatherDataList) {
}
