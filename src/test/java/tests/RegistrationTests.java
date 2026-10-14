package tests;

import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseErrorModel;
import models.registration.RegistrationResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.RegistrationSpec.*;

public class RegistrationTests extends TestBase {

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void successfulRegistrationResponseTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.password);

        RegistrationResponseModel registrationResponse = given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseModel.class);
        assertThat(registrationResponse.username()).isEqualTo(registrationData.username());
        assertThat(registrationResponse.id()).isNotNull();
        assertThat(registrationResponse.remoteAddr()).isNotEmpty();
    }

    @Test
    @DisplayName("Не заполнено поле username при регистрации")
    public void errorRegistrationEmptyUsernameTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.EMPTY_USERNAME, testData.password);
        RegistrationResponseErrorModel errorResponse = given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(errorRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseErrorModel.class);

        assertThat(errorResponse.username()).containsExactly("This field may not be blank.");
    }

    @Test
    @DisplayName("Не заполнено поле password при регистрации")
    public void errorRegistrationEmptyPasswordTest() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(testData.username, testData.EMPTY_PASSWORD);
        RegistrationResponseErrorModel errorResponse = given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(errorRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseErrorModel.class);

        assertThat(errorResponse.password()).containsExactly("This field may not be blank.");
    }

}