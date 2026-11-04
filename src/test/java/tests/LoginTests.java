package tests;

import models.login.LoginBodyModel;
import models.login.LoginResponseErrorModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class LoginTests extends TestBase {

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.password);

        LoginResponseModel loginResponse = api.auth.login(loginData);

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

        api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(testData.username, testData.wrongPassword);

        LoginResponseErrorModel errorResponse = api.auth.loginWithInvalidCredentials(loginData);

        step("Проверка ошибки 401 и сообщения о неверном password", () ->
                assertThat(errorResponse.detail()).contains("Invalid username or password."));
    }

    @Test
    @DisplayName("Ошибка авторизации: неверный логин")
    public void wrongUsernameLoginTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(testData.wrongPassword, testData.password);

        LoginResponseErrorModel errorResponse = api.auth.loginWithInvalidCredentials(loginData);

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

        LoginResponseErrorModel errorResponse = api.auth.loginWithInvalidBody(loginData);

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

        LoginResponseErrorModel errorResponse = api.auth.loginWithInvalidBody(body);

        step("Проверка ошибки 400 и сообщения об обязательном поле password", () ->
                assertThat(errorResponse.password()).contains("This field is required."));
    }

    @Test
    @DisplayName("Ошибка авторизации: пустое тело запроса (пустой JSON)")
    public void emptyJsonBodyLoginTest() {

        LoginResponseErrorModel errorResponse = api.auth.loginWithInvalidBody("{}");

        step("Проверка ошибки 400 и сообщения об обязательном наличии полей login и password", () -> {
            assertThat(errorResponse.username()).contains("This field is required.");
            assertThat(errorResponse.password()).contains("This field is required.");
        });
    }
}
