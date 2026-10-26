package tests;

import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutErrorResponseModel;
import models.logout.LogoutRequiredFieldErrorResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.LoginSpec.successfulLoginResponseSpec;
import static specs.LogoutSpec.*;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход с валидным токеном")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String refreshToken = step("Авторизация и получение токена", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("refresh");
        });

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        step("Успешный выход из системы", () -> {
            given(baseRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec);
        });
    }

    @Test
    @DisplayName("Ошибка выхода: невалидный refreshToken")
    public void logoutWithInvalidTokenTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(INVALID_REFRESH_TOKEN);

        LogoutErrorResponseModel errorResponse =
                step("Отправка запроса выхода из системы с невалидным refreshToken", () -> {
            return given(baseRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(invalidTokenLogoutResponseSpec)
                    .extract().as(LogoutErrorResponseModel.class);
        });

        step("Проверка ошибки и сообщения о неверном token", () -> {
            assertThat(errorResponse.detail()).isEqualTo("Token is invalid");
            assertThat(errorResponse.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Ошибка выхода: повторное использование токена")
    public void logoutTwiceWithSameTokenTest() {

        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String refreshToken = step("Авторизация и получение токенов", () -> {
            return given(baseRequestSpec)
                    .body(loginData)
                    .when()
                    .post("/auth/token/")
                    .then()
                    .spec(successfulLoginResponseSpec)
                    .extract().path("refresh");
        });

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        // Первый logout
        step("Первый выход из системы", () -> {
            given(baseRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(successfulLogoutResponseSpec);
        });

        // Второй logout
        LogoutErrorResponseModel errorResponse = step("Повторная попытка выхода из системы", () -> {
            return given(baseRequestSpec)
                    .body(logoutData)
                    .when()
                    .post("/auth/logout/")
                    .then()
                    .spec(invalidTokenLogoutResponseSpec)
                    .extract().as(LogoutErrorResponseModel.class);
        });

        step("Проверка ошибки и сообщения о token в blacklist", () -> {
            assertThat(errorResponse.detail()).isEqualTo("Token is blacklisted");
            assertThat(errorResponse.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Ошибка выхода: поле refresh отсутствует в теле")
    public void logoutWithoutRefreshFieldTest() {

        String body = "{}";

        LogoutRequiredFieldErrorResponseModel errorResponse =
                step("Отправка запроса выхода из системы с телом без refresh", () -> {
        return given(baseRequestSpec)
                .body(body)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(withoutRefreshFieldLogoutResponseSpec)
                .extract().as(LogoutRequiredFieldErrorResponseModel.class);
    });

        step("Проверка ошибки и сообщения об обязательном наличии поля refresh", () ->
        assertThat(errorResponse.refresh()).containsExactly("This field is required."));
    }
}