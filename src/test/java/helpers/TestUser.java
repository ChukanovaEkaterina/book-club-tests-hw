package helpers;

import models.login.LoginResponseModel;
import models.registration.RegistrationResponseModel;

public record TestUser(String username, LoginResponseModel tokens, RegistrationResponseModel profile) {
    public String accessToken() { return tokens.access(); }
}
