package api;

import io.qameta.allure.Step;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseErrorModel;
import models.registration.RegistrationResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.RegistrationSpec.errorRegistrationResponseSpec;

public class RegistrationApiClient {

    @Step("Регистрация нового пользователя")
    public void register(RegistrationBodyModel registrationBody) {
        given(baseRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec);
    }

    @Step("Отправка запроса на регистрацию ")
    public RegistrationResponseModel successfulRegistrationResponseTest(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию с пустым username")
    public RegistrationResponseErrorModel errorRegistrationEmptyUsernameTest(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(errorRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseErrorModel.class);
    }

    @Step("Отправка запроса на регистрацию с пустым password")
    public RegistrationResponseErrorModel errorRegistrationEmptyPasswordTest(RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(errorRegistrationResponseSpec)
                .extract()
                .as(RegistrationResponseErrorModel.class);
    }
}
