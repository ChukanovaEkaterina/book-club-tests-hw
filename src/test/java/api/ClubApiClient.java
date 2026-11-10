package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.club.ClubListResponseModel;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static specs.BaseSpec.baseRequestSpec;
import static specs.ClubSpec.*;

public class ClubApiClient {

    @Step("Получить список клубов")
    public ClubListResponseModel list(Map<String, ?> query) {
        return given(baseRequestSpec)
                .queryParams(query)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubListResponseSpec)
                .extract()
                .as(ClubListResponseModel.class);
    }

    @Step("Получить клуб")
    public Response get(int id, int expectedStatus) {
        return given(baseRequestSpec)
                .pathParam("id", id)
                .when()
                .get("/clubs/{id}/")
                .then()
                .statusCode(expectedStatus)
                .extract()
                .response();
    }

    @Step("Создать клуб")
    public ClubResponseModel create(ClubBodyModel body, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(body)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Обновить клуб методом PUT")
    public ClubResponseModel update(ClubBodyModel body, int id, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .body(body)
                .when()
                .put("/clubs/{id}/")
                .then()
                .spec(successfulUpdateClubResponseSpec)
                .extract()
                .as(ClubResponseModel.class);
    }

    @Step("Удалить клуб")
    public void delete(int id, String accessToken) {
        given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/")
                .then()
                .statusCode(204)
                .body(emptyString());
    }
}
