package tests;

import models.club.ClubListResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.login.LoginBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.ClubSpec.*;
import static tests.TestData.REGISTERED_USERNAME;
import static tests.TestData.REGISTERED_PASSWORD;
import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;

public class ClubTests extends TestBase {

    @Test
    @DisplayName("Успешное получение списка клубов")
    public void successfulClubListTest() {
        ClubListResponseModel response = api.clubs.list(Map.of());

        step("Проверка данных списка клубов", () -> {
            assertThat(response.count()).isGreaterThanOrEqualTo(0);
            assertThat(response.results()).isNotNull();
            assertThat(response.results().size()).isLessThanOrEqualTo(response.count());
            assertThat(response.results()).allSatisfy(club -> {
                assertThat(club.id()).isGreaterThan(0);
                assertThat(club.bookTitle()).isNotBlank();
                assertThat(club.bookAuthors()).isNotBlank();
                assertThat(club.owner()).isGreaterThan(0);
            });
        });
    }

    @Test
    @DisplayName("Ошибка получения списка клубов: нулевой номер страницы")
    public void clubListWithZeroPageTest() {
        String detail = given(baseRequestSpec)
                .queryParam("page", 0)
                .get("/clubs/")
                .then()
                .spec(invalidPageClubListResponseSpec)
                .extract()
                .path("detail");

        step("Проверка ошибки 404 и сообщения о неверной странице", () ->
                assertThat(detail).isEqualTo("Invalid page."));
    }

    @Test
    @DisplayName("Успешное создание клуба")
    public void successfulCreateClubTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);
        String accessToken = api.auth.getAccessToken(loginData);

        ClubBodyModel clubData = new ClubBodyModel(
                testData.bookTitle,
                testData.bookAuthors,
                testData.publicationYear,
                testData.description,
                testData.TELEGRAM_CHAT_LINK
        );

        ClubResponseModel response = api.clubs.create(clubData, accessToken);

        step("Проверка данных созданного клуба", () -> {
            assertThat(response.id()).isGreaterThan(0);
            assertThat(response.bookTitle()).isEqualTo(clubData.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(clubData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(clubData.publicationYear());
            assertThat(response.description()).isEqualTo(clubData.description());
            assertThat(response.telegramChatLink()).isEqualTo(clubData.telegramChatLink());
            assertThat(response.owner()).isGreaterThan(0);
            assertThat(response.members()).contains(response.owner());
            assertThat(response.reviews()).isEmpty();
            assertThat(response.created()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Успешное обновление клуба")
    public void successfulUpdateClubTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);
        String accessToken = api.auth.getAccessToken(loginData);

        ClubBodyModel clubData = new ClubBodyModel(
                testData.bookTitle,
                testData.bookAuthors,
                testData.publicationYear,
                testData.description,
                testData.TELEGRAM_CHAT_LINK
        );
        ClubResponseModel createdClub = api.clubs.create(clubData, accessToken);

        ClubBodyModel updateData = new ClubBodyModel(
                testData.updateBookTitle,
                testData.updateBookAuthors,
                testData.updatePublicationYear,
                testData.updateDescription,
                testData.UPDATE_TELEGRAM_CHAT_LINK
        );

        ClubResponseModel response = api.clubs.update(updateData, createdClub.id(), accessToken);

        step("Проверка данных обновленного клуба", () -> {
            assertThat(response.id()).isEqualTo(createdClub.id());
            assertThat(response.bookTitle()).isEqualTo(updateData.bookTitle());
            assertThat(response.bookAuthors()).isEqualTo(updateData.bookAuthors());
            assertThat(response.publicationYear()).isEqualTo(updateData.publicationYear());
            assertThat(response.description()).isEqualTo(updateData.description());
            assertThat(response.telegramChatLink()).isEqualTo(updateData.telegramChatLink());
            assertThat(response.owner()).isEqualTo(createdClub.owner());
            assertThat(response.members()).containsExactlyInAnyOrderElementsOf(createdClub.members());
            assertThat(response.reviews()).isEmpty();
            assertThat(response.created()).isEqualTo(createdClub.created());
            assertThat(response.modified()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Успешное удаление клуба")
    public void successfulDeleteClubTest() {
        LoginBodyModel loginData = new LoginBodyModel(REGISTERED_USERNAME, REGISTERED_PASSWORD);
        String accessToken = api.auth.getAccessToken(loginData);

        ClubBodyModel clubData = new ClubBodyModel(
                testData.bookTitle,
                testData.bookAuthors,
                testData.publicationYear,
                testData.description,
                testData.TELEGRAM_CHAT_LINK
        );
        ClubResponseModel createdClub = api.clubs.create(clubData, accessToken);

        api.clubs.delete(createdClub.id(), accessToken);

        String detail = api.clubs.get(createdClub.id(), 404).path("detail");

        step("Проверка ошибки 404 и сообщения о том, что клуб не найден", () ->
                assertThat(detail).isEqualTo("No Club matches the given query."));
    }
}
