package tests;

import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseErrorModel;
import models.registration.RegistrationResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationTests extends TestBase {

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void successfulRegistrationResponseTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        step("Проверка данных зарегистрированного пользователя", () -> {
            assertThat(registrationResponse.username()).isEqualTo(registrationData.username());
            assertThat(registrationResponse.id()).isNotNull();
            assertThat(registrationResponse.remoteAddr()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("Не заполнено поле username при регистрации")
    public void errorRegistrationEmptyUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.EMPTY_USERNAME, testData.password);
        RegistrationResponseErrorModel errorResponse =
                api.registration.errorRegistrationEmptyUsernameTest(registrationData);

        step("Проверка ответа 400 и сообщения о пустом username", () ->
                assertThat(errorResponse.username()).containsExactly("This field may not be blank."));
    }

    @Test
    @DisplayName("Не заполнено поле password при регистрации")
    public void errorRegistrationEmptyPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.EMPTY_PASSWORD);
        RegistrationResponseErrorModel errorResponse = api.registration.errorRegistrationEmptyPasswordTest(registrationData);

        step("Проверка ответа 400 и сообщения о пустом password", () ->
                assertThat(errorResponse.password()).containsExactly("This field may not be blank."));
    }
}
