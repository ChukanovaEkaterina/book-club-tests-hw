package tests;

import models.login.LoginBodyModel;
import models.logout.LogoutBodyModel;
import models.logout.LogoutErrorResponseModel;
import models.logout.LogoutRequiredFieldErrorResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    @Test
    @DisplayName("Успешный выход с валидным токеном")
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String refreshToken = api.auth.getRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        api.auth.logout(logoutData);
    }

    @Test
    @DisplayName("Ошибка выхода: невалидный refreshToken")
    public void logoutWithInvalidTokenTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(INVALID_REFRESH_TOKEN);

        LogoutErrorResponseModel errorResponse = api.auth.logoutWithInvalidToken(logoutData);

        step("Проверка ошибки и сообщения о неверном token", () -> {
            assertThat(errorResponse.detail()).isEqualTo("Token is invalid");
            assertThat(errorResponse.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Ошибка выхода: повторное использование токена")
    public void logoutTwiceWithSameTokenTest() {

        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);

        String refreshToken = api.auth.getRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        // Первый logout
        api.auth.logout(logoutData);

        // Второй logout
        LogoutErrorResponseModel errorResponse = api.auth.logoutWithInvalidToken(logoutData);

        step("Проверка ошибки и сообщения о token в blacklist", () -> {
            assertThat(errorResponse.detail()).isEqualTo("Token is blacklisted");
            assertThat(errorResponse.code()).isEqualTo("token_not_valid");
        });
    }

    @Test
    @DisplayName("Ошибка выхода: поле refresh отсутствует в теле")
    public void logoutWithoutRefreshFieldTest() {

        String body = "{}";

        LogoutRequiredFieldErrorResponseModel errorResponse = api.auth.logoutWithoutRefreshField(body);

        step("Проверка ошибки и сообщения об обязательном наличии поля refresh", () ->
                assertThat(errorResponse.refresh()).containsExactly("This field is required."));
    }
}
