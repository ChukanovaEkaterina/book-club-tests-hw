package models.localstorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.registration.RegistrationResponseModel;

public record AuthModel(RegistrationResponseModel user, String accessToken,
                        String refreshToken, boolean isAuthenticated) {

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize browser authorization to JSON", e);
        }
    }
}
