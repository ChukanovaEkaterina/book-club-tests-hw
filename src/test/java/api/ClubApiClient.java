package api;

import io.qameta.allure.Step;
import models.club.ClubListResponseModel;
import models.club.ClubListErrorResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.club.UpdateClubBodyModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.ClubSpec.successfulClubListResponseSpec;
import static specs.ClubSpec.invalidPageClubListResponseSpec;
import static specs.ClubSpec.successfulCreateClubResponseSpec;
import static specs.ClubSpec.successfulUpdateClubResponseSpec;
import static specs.ClubSpec.successfulDeleteClubResponseSpec;
import static specs.ClubSpec.clubNotFoundResponseSpec;

public class ClubApiClient {

    @Step("Отправка запроса на получение списка клубов")
    public ClubListResponseModel successfulClubListTest() {
        return given(baseRequestSpec)
                .when()
                .get("/clubs/")
                .then()
                .spec(successfulClubListResponseSpec)
                .extract().as(ClubListResponseModel.class);
    }

    @Step("Отправка запроса на получение списка клубов с нулевым номером страницы")
    public ClubListErrorResponseModel clubListWithZeroPageTest() {
        return given(baseRequestSpec)
                .queryParam("page", 0)
                .when()
                .get("/clubs/")
                .then()
                .spec(invalidPageClubListResponseSpec)
                .extract().as(ClubListErrorResponseModel.class);
    }

    @Step("Отправка запроса на создание клуба")
    public ClubResponseModel successfulCreateClubTest(ClubBodyModel clubData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .body(clubData)
                .when()
                .post("/clubs/")
                .then()
                .spec(successfulCreateClubResponseSpec)
                .extract().as(ClubResponseModel.class);
    }

    @Step("Отправка запроса на обновление клуба методом PUT")
    public ClubResponseModel successfulUpdateClubTest(UpdateClubBodyModel updateData, int id, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .body(updateData)
                .when()
                .put("/clubs/{id}/")
                .then()
                .spec(successfulUpdateClubResponseSpec)
                .extract().as(ClubResponseModel.class);
    }

    @Step("Отправка запроса на удаление клуба методом DELETE")
    public void successfulDeleteClubTest(int id, String accessToken) {
        given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .delete("/clubs/{id}/")
                .then()
                .spec(successfulDeleteClubResponseSpec);
    }

    @Step("Отправка запроса на получение удаленного клуба")
    public ClubListErrorResponseModel getDeletedClub(int id, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", "Bearer " + accessToken)
                .pathParam("id", id)
                .when()
                .get("/clubs/{id}/")
                .then()
                .spec(clubNotFoundResponseSpec)
                .extract().as(ClubListErrorResponseModel.class);
    }
}
