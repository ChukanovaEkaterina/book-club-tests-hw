package tests;

import models.login.LoginBodyModel;
import models.login.LoginResponseErrorModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.LoginSpec.*;
import static specs.RegistrationSpec.successfulRegistrationResponseSpec;

public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.password);

        LoginResponseModel loginResponse = given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(LoginResponseModel.class);


        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(testData.FIXED_TOKEN_PART);
        assertThat(actualRefresh).startsWith(testData.FIXED_TOKEN_PART);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);
    }

    @Test
    @DisplayName("Ошибка авторизации: неверный пароль")
    public void wrongPasswordLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.wrongPassword);

        LoginResponseErrorModel errorResponse = given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongPasswordAndUsernameLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);

        assertThat(errorResponse.detail()).contains("Invalid username or password.");
    }

    @Test
    @DisplayName("Ошибка авторизации: неверный логин")
    public void wrongUsernameLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);

        LoginBodyModel loginData = new LoginBodyModel(testData.wrongPassword, testData.password);

        LoginResponseErrorModel errorResponse = given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongPasswordAndUsernameLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);

        assertThat(errorResponse.detail()).contains("Invalid username or password.");
    }

    @Test
    @DisplayName("Ошибка авторизации: пустое поле логина и пароля")
    public void emptyFieldUsernameAndPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(
                testData.EMPTY_USERNAME,
                testData.EMPTY_PASSWORD
        );

        LoginResponseErrorModel errorResponse = given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);

        assertThat(errorResponse.username()).contains("This field may not be blank.");
        assertThat(errorResponse.password()).contains("This field may not be blank.");
    }

    @Test
    @DisplayName("Ошибка авторизации: отсутствует поле password")
    public void missingPasswordFieldLoginTest() {

        String body = """
            {
              "username": "%s"
            }
            """.formatted(testData.username);

        LoginResponseErrorModel errorResponse = given(baseRequestSpec)
                .body(body)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);

        assertThat(errorResponse.password()).contains("This field is required.");
    }
    @Test
    @DisplayName("Ошибка авторизации: пустое тело запроса (пустой JSON)")
    public void emptyJsonBodyLoginTest() {

        LoginResponseErrorModel errorResponse = given(baseRequestSpec)
                .body("{}")
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyFieldLoginResponseSpec)
                .extract().as(LoginResponseErrorModel.class);

        assertThat(errorResponse.username()).contains("This field is required.");
        assertThat(errorResponse.password()).contains("This field is required.");
    }
}
