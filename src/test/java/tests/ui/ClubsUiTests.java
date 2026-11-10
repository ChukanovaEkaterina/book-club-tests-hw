package tests.ui;

import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.localstorage.AuthModel;
import models.login.LoginBodyModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ClubPage;
import pages.ClubsPage;
import pages.ClubFormPage;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class ClubsUiTests extends UiTestBase {

    String username;
    String password;

    String bookTitle;
    String bookAuthors;
    Integer publicationYear;
    String description;
    String telegramChatLink;

    private final ClubPage clubPage = new ClubPage();
    private final ClubsPage mainPage = new ClubsPage();
    private final ClubFormPage createClubPage = new ClubFormPage();

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = "autotest_" + UUID.randomUUID().toString().replace("-", "");
        password = UUID.randomUUID() + "Aa1!";

        bookTitle = faker.book().title() + " " + UUID.randomUUID();
        bookAuthors = faker.book().author();
        publicationYear = faker.number().numberBetween(2000, 2026);
        description = faker.lorem().sentence();
        telegramChatLink = "https://t.me/qa_guru_" + faker.internet().uuid();
    }

    @Test
    @DisplayName("[API + UI] Отображение данных измененных через API")
    public void updatedClubIsVisibleInUi() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        ClubBodyModel updateClubBody = new ClubBodyModel(
                bookTitle + " Updated",
                bookAuthors + " Updated",
                publicationYear + 1,
                description + " Updated",
                "https://t.me/qa_guru_updated"
        );
        api.clubs.update(updateClubBody, createdClub.id(), accessToken);

        clubPage.openClub(createdClub.id());

        step("Проверяем обновлённые данные клуба", () -> {
            clubPage.checkDetails(updateClubBody);
        });
    }

    @Test
    @DisplayName("[API + UI] Поиск и просмотр клуба, добавленного через API")
    public void apiClubIsVisibleInUi() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        mainPage.openPage();
        mainPage.searchClub(bookTitle);

        step("Проверяем найденный клуб", () -> {
            mainPage.checkClub(bookTitle, bookAuthors);
        });

        clubPage.openClub(createdClub.id());

        step("Проверяем данные клуба и права гостя", () -> {
            clubPage.checkDetails(createClubBody);
            clubPage.checkNoEdit();
        });
    }

    @Test
    @DisplayName("[API + UI] Удалённый клуб отсутствует в поиске")
    public void deletedClubDisappears() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        mainPage.openPage();
        mainPage.searchClub(bookTitle);
        mainPage.checkClub(bookTitle, bookAuthors);

        api.clubs.delete(createdClub.id(), accessToken);

        mainPage.openPage();
        mainPage.searchClub(bookTitle);

        step("Проверяем отсутствие удалённого клуба", () -> {
            mainPage.checkNoResults();
        });
    }

    @Test
    @DisplayName("[API + UI] Создание клуба")
    public void successfulCreateClub() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        String localStorageAuthBody = new AuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        createClubPage.openCreateClubPage();
        step("Заполняем форму данными для нового клуба", () -> {
            createClubPage.enterBookTitle(bookTitle);
            createClubPage.enterBookAuthors(bookAuthors);
            createClubPage.enterPublicationYear(publicationYear.toString());
            createClubPage.enterDescription(description);
            createClubPage.enterTelegramChatLink(telegramChatLink);
        });

        createClubPage.clickSubmitButton();

        step("Находим созданный клуб и проверяем его данные", () -> {
            mainPage.searchClub(bookTitle);
            mainPage.checkExistClub(bookTitle);
            mainPage.checkCreatedClubData(bookTitle, publicationYear.toString(), bookAuthors, description);

            List<ClubResponseModel> createdClubs = api.clubs.list(Map.of("search", bookTitle)).results();
            assertThat(createdClubs).singleElement().satisfies(clubResponse -> {
                assertThat(clubResponse.owner()).isEqualTo(registrationResponse.id());
                assertThat(clubResponse.bookTitle()).isEqualTo(bookTitle);
                assertThat(clubResponse.bookAuthors()).isEqualTo(bookAuthors);
                assertThat(clubResponse.publicationYear()).isEqualTo(publicationYear);
                assertThat(clubResponse.description()).isEqualTo(description);
                assertThat(clubResponse.telegramChatLink()).isEqualTo(telegramChatLink);
            });
        });

    }
}
