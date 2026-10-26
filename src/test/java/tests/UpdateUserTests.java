package tests;

import models.login.LoginBodyModel;
import models.user.UpdateUserBodyModel;
import models.user.UpdateUserErrorResponseModel;
import models.user.UpdateUserResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.LoginSpec.successfulLoginResponseSpec;
import static specs.UserSpec.*;
import static tests.TestData.*;

public class UpdateUserTests extends TestBase {

    @Test
    @DisplayName("Успешное обновление профиля")
    public void successfulUpdateUserTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String accessToken = step("Авторизация и получение токена", () -> {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("access");
    });

        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                REGISTERED_USERNAME,
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        UpdateUserResponseModel response = step("Отправка запроса на обновление профиля методом PUT", () -> {
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(updateData)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(successfulUpdateUserResponseSpec)
                            .extract().as(UpdateUserResponseModel.class);
                });

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

        String accessToken = step("Авторизация и получение токена", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("access");
        });

        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                "",
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        UpdateUserErrorResponseModel errorResponse =
                step("Отправка запроса на обновление профиля с пустым username", () -> {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(badRequestUpdateUserResponseSpec)
                .extract().as(UpdateUserErrorResponseModel.class);
    });


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

        step("Отправка запроса на обновление профиля с невалидным accessToken", () -> {
            given(baseRequestSpec)
                    .header("Authorization", "Bearer invalidToken")
                    .body(updateData)
                    .when()
                    .put("/users/me/")
                    .then()
                    .spec(unauthorizedUpdateUserResponseSpec);
        });
    }

    @Test
    @DisplayName("Ошибка обновления: пустое тело запроса")
    public void updateWithEmptyBodyTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String accessToken = step("Авторизация и получение токена", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("access");
        });

        UpdateUserErrorResponseModel errorResponse =
                step("Отправка запроса на обновление профиля с пустым JSON)", () -> {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body("{}")
                .when()
                .put("/users/me/")
                .then()
                .spec(badRequestUpdateUserResponseSpec)
                .extract().as(UpdateUserErrorResponseModel.class);
    });

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

        String accessToken = step("Авторизация и получение токена", () -> {
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().path("access");
                });

        UpdateUserBodyModel updateData = new UpdateUserBodyModel(
                REGISTERED_USERNAME,
                testData.updateFirstName,
                testData.updateLastName,
                testData.updateEmail
        );

        UpdateUserResponseModel response = step("Отправка запроса на обновление профиля методом PATCH", () -> {
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(updateData)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(successfulUpdateUserResponseSpec)
                            .extract().as(UpdateUserResponseModel.class);
                });

        step("Проверка обновленния данных профиля", () -> {
            assertThat(response.username()).isEqualTo(REGISTERED_USERNAME);
            assertThat(response.firstName()).isEqualTo(testData.updateFirstName);
            assertThat(response.lastName()).isEqualTo(testData.updateLastName);
            assertThat(response.email()).isEqualTo(testData.updateEmail);
        });
    }
}