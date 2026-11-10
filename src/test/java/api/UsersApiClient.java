package api;

import io.qameta.allure.Step;
import models.user.UpdateUserBodyModel;
import models.user.UpdateUserErrorResponseModel;
import models.user.UpdateUserResponseModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.UserSpec.*;

public class UsersApiClient {
    @Step("Отправка запроса на обновление профиля методом PUT")
    public UpdateUserResponseModel successfulUpdateUserTest(UpdateUserBodyModel updateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(UpdateUserResponseModel.class);
    }

    @Step("Отправка запроса на обновление профиля с пустым username")
    public UpdateUserErrorResponseModel updateWithEmptyUsernameTest(UpdateUserBodyModel updateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(badRequestUpdateUserResponseSpec)
                .extract().as(UpdateUserErrorResponseModel.class);
    }

    @Step("Отправка запроса на обновление профиля с невалидным accessToken")
    public void updateWithInvalidTokenTest(UpdateUserBodyModel updateData) {
        given(baseRequestSpec)
                .header("Authorization", "Bearer invalidToken")
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(unauthorizedUpdateUserResponseSpec);
    }

    @Step("Отправка запроса на обновление профиля с пустым JSON)")
    public UpdateUserErrorResponseModel updateWithEmptyBodyTest(String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body("{}")
                .when()
                .put("/users/me/")
                .then()
                .spec(badRequestUpdateUserResponseSpec)
                .extract().as(UpdateUserErrorResponseModel.class);
    }

    @Step("Отправка запроса на обновление профиля методом PATCH")
    public UpdateUserResponseModel successfulPatchUserTest(UpdateUserBodyModel updateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateData)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulUpdateUserResponseSpec)
                .extract().as(UpdateUserResponseModel.class);
    }
}
