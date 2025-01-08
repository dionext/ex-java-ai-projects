package com.dionext.ai.ai_advanced.model.travel;

import java.util.List;

public record TripRecommendationResponse(
        List<PlaceRecommendation> placesToVisit,
        List<TravelItineraryItem> travelPlan) {
}
