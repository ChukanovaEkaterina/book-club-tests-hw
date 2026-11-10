package tests;

import models.login.LoginBodyModel;
import models.user.UpdateUserBodyModel;
import models.user.UpdateUserErrorResponseModel;
import models.user.UpdateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class UpdateUserTests extends TestBase {

    @Test
    @DisplayName("Успешное обновление профиля")
    public void successfulUpdateUserTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String accessToken = api.auth.getAccessToken(loginData);

        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                REGISTERED_USERNAME,
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        UpdateUserResponseModel response = api.users.successfulUpdateUserTest(updateData, accessToken);

        step("Проверка обновленния данных профиля", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.username()).isEqualTo(REGISTERED_USERNAME);
            assertThat(response.firstName()).isEqualTo(testData.updateFirstName);
            assertThat(response.lastName()).isEqualTo(testData.updateLastName);
            assertThat(response.email()).isEqualTo(testData.updateEmail);
            assertThat(response.remoteAddr()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Ошибка обновления: пустой username")
    public void updateWithEmptyUsernameTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String accessToken = api.auth.getAccessToken(loginData);

        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                "",
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        UpdateUserErrorResponseModel errorResponse =
                api.users.updateWithEmptyUsernameTest(updateData, accessToken);

        step("Проверка ответа (400) и сообщения о пустом username", () ->
                assertThat(errorResponse.username()).contains("This field may not be blank."));
    }

    @Test
    @DisplayName("Ошибка обновления: невалидный accessToken")
    public void updateWithInvalidTokenTest() {
        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                REGISTERED_USERNAME,
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        api.users.updateWithInvalidTokenTest(updateData);
    }

    @Test
    @DisplayName("Ошибка обновления: пустое тело запроса")
    public void updateWithEmptyBodyTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String accessToken = api.auth.getAccessToken(loginData);

        UpdateUserErrorResponseModel errorResponse =
                api.users.updateWithEmptyBodyTest(accessToken);

        step("Проверка ошибки 400 и сообщения об обязательном наличии полей", () -> {
            assertThat(errorResponse.username()).contains("This field is required.");
            assertThat(errorResponse.email()).contains("This field is required.");
            assertThat(errorResponse.firstName()).contains("This field is required.");
            assertThat(errorResponse.lastName()).contains("This field is required.");
        });
    }

    @Test
    @DisplayName("Успешное обновление профиля пользователя (PATCH)")
    public void successfulPatchUserTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String accessToken = api.auth.getAccessToken(loginData);

        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                REGISTERED_USERNAME,
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        UpdateUserResponseModel response = api.users.successfulPatchUserTest(updateData, accessToken);

        step("Проверка обновленния данных профиля", () -> {
            assertThat(response.username()).isEqualTo(REGISTERED_USERNAME);
            assertThat(response.firstName()).isEqualTo(testData.updateFirstName);
            assertThat(response.lastName()).isEqualTo(testData.updateLastName);
            assertThat(response.email()).isEqualTo(testData.updateEmail);
        });
    }
}
