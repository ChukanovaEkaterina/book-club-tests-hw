package api;

import io.qameta.allure.Step;
import models.login.LoginBodyModel;
import models.login.LoginResponseErrorModel;
import models.login.LoginResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutErrorResponseModel;
import models.logout.LogoutRequiredFieldErrorResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.LoginSpec.emptyFieldLoginResponseSpec;
import static specs.LoginSpec.successfulLoginResponseSpec;
import static specs.LoginSpec.wrongPasswordAndUsernameLoginResponseSpec;
import static specs.LogoutSpec.invalidTokenLogoutResponseSpec;
import static specs.LogoutSpec.successfulLogoutResponseSpec;
import static specs.LogoutSpec.withoutRefreshFieldLogoutResponseSpec;

public class AuthApiClient {

    @Step("Авторизация")
    public LoginResponseModel login(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(LoginResponseModel.class);
    }

    @Step("Авторизация и получение токена")
    public String getRefreshToken(LoginBodyModel loginBody) {
        return login(loginBody).refresh();
    }

    @Step("Авторизация с неверным логином или паролем")
    public LoginResponseErrorModel loginWithInvalidCredentials(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongPasswordAndUsernameLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);
    }

    @Step("Авторизация с пустыми логином и паролем")
    public LoginResponseErrorModel loginWithInvalidBody(LoginBodyModel loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);
    }

    @Step("Авторизация с отсутствующими обязательными полями")
    public LoginResponseErrorModel loginWithInvalidBody(String loginBody) {
        return given(baseRequestSpec)
                .body(loginBody)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);
    }

    @Step("Успешный выход из системы")
    public void logout(LogoutBodyModel logoutBody) {
        given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    @Step("Выход с невалидным или использованным refresh-токеном")
    public LogoutErrorResponseModel logoutWithInvalidToken(LogoutBodyModel logoutBody) {
        return given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidTokenLogoutResponseSpec)
                .extract().as(LogoutErrorResponseModel.class);
    }

    @Step("Выход без обязательного поля refresh")
    public LogoutRequiredFieldErrorResponseModel logoutWithoutRefreshField(String logoutBody) {
        return given(baseRequestSpec)
                .body(logoutBody)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(withoutRefreshFieldLogoutResponseSpec)
                .extract().as(LogoutRequiredFieldErrorResponseModel.class);
    }

    @Step("Авторизация и получение токена")
    public String getAccessToken(LoginBodyModel loginData) {
        return login(loginData).access();
    }
}
