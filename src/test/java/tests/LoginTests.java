package tests;

import models.login.LoginBodyModel;
import models.login.LoginResponseErrorModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
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

        step("Регистрация нового пользователя", () -> {
            given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.password);

        LoginResponseModel loginResponse = step("Авторизация и получение токенов", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().as(LoginResponseModel.class);
        });

        step("Проверка формата access и refresh токенов", () -> {

            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(testData.FIXED_TOKEN_PART);
            assertThat(actualRefresh).startsWith(testData.FIXED_TOKEN_PART);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);
        });
    }

    @Test
    @DisplayName("Ошибка авторизации: неверный пароль")
    public void wrongPasswordLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        step("Регистрация нового пользователя", () -> {
            given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.wrongPassword);

        LoginResponseErrorModel errorResponse = step("Отправка запроса авторизации с неверным password", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongPasswordAndUsernameLoginResponseSpec)
                    .extract().as(LoginResponseErrorModel.class);
        });

        step("Проверка ошибки 401 и сообщения о неверном password", () ->
                assertThat(errorResponse.detail()).contains("Invalid username or password."));
    }

    @Test
    @DisplayName("Ошибка авторизации: неверный логин")
    public void wrongUsernameLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        step("Регистрация нового пользователя", () -> {
            given(baseRequestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successfulRegistrationResponseSpec);
        });

        LoginBodyModel loginData = new LoginBodyModel(testData.wrongPassword, testData.password);

        LoginResponseErrorModel errorResponse = step("Отправка запроса авторизации с неверным login", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(wrongPasswordAndUsernameLoginResponseSpec)
                    .extract().as(LoginResponseErrorModel.class);
        });

        step("Проверка ошибки 401 и сообщения о неверном login", () ->
                assertThat(errorResponse.detail()).contains("Invalid username or password."));
    }

    @Test
    @DisplayName("Ошибка авторизации: пустое поле логина и пароля")
    public void emptyFieldUsernameAndPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(
                testData.EMPTY_USERNAME,
                testData.EMPTY_PASSWORD
        );

        LoginResponseErrorModel errorResponse = step("Отправка запроса авторизации с пустыми login и password", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyFieldLoginResponseSpec)
                    .extract().as(LoginResponseErrorModel.class);
        });

        step("Проверка ошибки 400 и сообщения об обязательных заполненных полях login и password", () -> {
            assertThat(errorResponse.username()).contains("This field may not be blank.");
            assertThat(errorResponse.password()).contains("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Ошибка авторизации: отсутствует поле password")
    public void missingPasswordFieldLoginTest() {

        String body = """
                {
                  "username": "%s"
                }
                """.formatted(testData.username);

        LoginResponseErrorModel errorResponse = step("Отправка запроса авторизации без поля password", () -> {
            return given(baseRequestSpec)
                    .body(body)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyFieldLoginResponseSpec)
                    .extract().as(LoginResponseErrorModel.class);
        });

        step("Проверка ошибки 400 и сообщения об обязательном поле password", () ->
                assertThat(errorResponse.password()).contains("This field is required."));
    }

    @Test
    @DisplayName("Ошибка авторизации: пустое тело запроса (пустой JSON)")
    public void emptyJsonBodyLoginTest() {

        LoginResponseErrorModel errorResponse = step("Отправка запроса авторизации с пустым JSON", () -> {
            return given(baseRequestSpec)
                    .body("{}")
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(emptyFieldLoginResponseSpec)
                    .extract().as(LoginResponseErrorModel.class);
        });

        step("Проверка ошибки 400 и сообщения об обязательном наличии полей login и password", () -> {
            assertThat(errorResponse.username()).contains("This field is required.");
            assertThat(errorResponse.password()).contains("This field is required.");
        });
    }
}
