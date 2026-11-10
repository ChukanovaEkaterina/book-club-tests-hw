package tests.ui;

import helpers.TestUser;
import models.reviews.ReviewResponseModel;
import models.club.ClubBodyModel;
import models.club.ClubResponseModel;
import models.localstorage.AuthModel;
import models.login.LoginBodyModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseModel;
import models.reviews.ReviewBodyModel;
import models.reviews.ReviewsListResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ClubPage;
import pages.ReviewClubPage;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.localStorage;
import static com.codeborne.selenide.Selenide.open;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.ReviewsSpec.successfulReviewsListResponseSpec;
import static org.assertj.core.api.Assertions.assertThat;

public class ReviewsUiTests extends UiTestBase {

    String username;
    String password;

    String bookTitle;
    String bookAuthors;
    Integer publicationYear;
    String description;
    String telegramChatLink;

    String review;
    Integer assessment;
    Integer readPages;

    private final ClubPage clubPage = new ClubPage();
    private final ReviewClubPage reviewPage = new ReviewClubPage();

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = "autotest_" + UUID.randomUUID().toString().replace("-", "");
        password = UUID.randomUUID() + "Aa1!";

        bookTitle = faker.book().title() + " " + UUID.randomUUID();
        bookAuthors = faker.book().author();
        publicationYear = faker.number().numberBetween(1900, 2026);
        description = faker.lorem().sentence();
        telegramChatLink = "https://t.me/qa_guru_" + faker.internet().uuid();

        review = faker.lorem().sentence();
        assessment = 4;
        readPages = 120;
    }

    @Test
    @DisplayName("[API + UI] Отображение отзыва, созданного через API")
    public void reviewFromApiIsVisible() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClub.id(),
                review,
                assessment,
                readPages
        );
        api.reviews.createReview(accessToken, reviewBody);

        String localStorageAuthBody = new AuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClub(createdClub.id());

        step("Проверяем автора и данные отзыва", () -> {
            reviewPage.checkReview(username, review, assessment, readPages);
        });
    }

    @Test
    @DisplayName("[API + UI] Нет отзыва после удаления через API")
    public void deletedReviewDisappears() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClub.id(),
                review,
                assessment,
                readPages
        );
        ReviewResponseModel createdReview = api.reviews.createReview(accessToken, reviewBody);

        String localStorageAuthBody = new AuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClub(createdClub.id());

        step("Проверяем отзыв перед удалением", () -> {
            reviewPage.checkReview(username, review, assessment, readPages);
        });

        api.reviews.deleteReview(accessToken, createdReview.id());
        clubPage.openClub(createdClub.id());

        step("Проверяем отсутствие удалённого отзыва", () -> {
            reviewPage.checkEmpty();
        });
    }

    @Test
    @DisplayName("[API + UI] Создание отзыва")
    public void createReviewInUi() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        String localStorageAuthBody = new AuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClub(createdClub.id());

        reviewPage.clickAddReviewButton();
        step("Заполняем форму данными для отзыва", () -> {
            reviewPage.enterAssessment(5);
            reviewPage.enterReadPages(250);
            reviewPage.enterReview(review);
        });
        reviewPage.clickSaveReviewButton();

        step("Проверяем добавление отзыва и сохранённые данные", () -> {
            reviewPage.checkReview(username, review, 5, 250);

            ReviewsListResponseModel reviewList = given(baseRequestSpec).queryParam("club", createdClub.id()).get("/clubs/reviews/").then()
                    .spec(successfulReviewsListResponseSpec).extract().as(ReviewsListResponseModel.class);
            assertThat(reviewList.results()).singleElement().satisfies(createdReview -> {
                assertThat(createdReview.review()).isEqualTo(review);
                assertThat(createdReview.user().id()).isEqualTo(registrationResponse.id());
                assertThat(createdReview.assessment()).isEqualTo(5);
                assertThat(createdReview.readPages()).isEqualTo(250);
            });
        });
    }

    @Test
    @DisplayName("[API + UI] Редактирование своего отзыва")
    public void editReviewInUi() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClub.id(),
                review,
                assessment,
                readPages
        );
        ReviewResponseModel createdReview = api.reviews.createReview(accessToken, reviewBody);

        String localStorageAuthBody = new AuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClub(createdClub.id());

        String updatedReview = "Новые впечатления";
        reviewPage.edit(username, updatedReview, 3, 300);

        step("Проверяем изменение отзыва в UI и API", () -> {
            reviewPage.checkReview(username, updatedReview, 3, 300);

            ReviewResponseModel reviewResponse = api.reviews.getReview(createdReview.id());
            assertThat(reviewResponse.review()).isEqualTo(updatedReview);
            assertThat(reviewResponse.assessment()).isEqualTo(3);
            assertThat(reviewResponse.readPages()).isEqualTo(300);
        });
    }

    @Test
    @DisplayName("[API + UI] Удаление своего отзыва")
    public void deleteReviewInUi() {
        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
        RegistrationResponseModel registrationResponse = api.registration.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);
        LoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = loginResponse.access();
        String refreshToken = loginResponse.refresh();

        ClubBodyModel createClubBody = new ClubBodyModel(
                bookTitle,
                bookAuthors,
                publicationYear,
                description,
                telegramChatLink
        );
        ClubResponseModel createdClub = api.clubs.create(createClubBody, accessToken);

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClub.id(),
                review,
                assessment,
                readPages
        );
        ReviewResponseModel createdReview = api.reviews.createReview(accessToken, reviewBody);

        String localStorageAuthBody = new AuthModel(registrationResponse, accessToken, refreshToken, true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClub(createdClub.id());

        step("Проверяем автора и данные отзыва", () -> {
            reviewPage.checkReview(username, review, assessment, readPages);
        });

        reviewPage.delete(username);

        step("Проверяем удаление отзыва в UI и API", () -> {
            reviewPage.checkEmpty();
            given(baseRequestSpec).get("/clubs/reviews/{id}/", createdReview.id()).then().statusCode(404).extract().response();
        });
    }

    @Test
    @DisplayName("[API + UI] Другой пользователь не может изменить или удалить отзыв")
    public void anotherUserCannotEditReview() {
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

        ReviewBodyModel reviewBody = new ReviewBodyModel(
                createdClub.id(),
                review,
                assessment,
                readPages
        );
        api.reviews.createReview(accessToken, reviewBody);

        TestUser otherUser = createUser();
        String localStorageAuthBody = new AuthModel(otherUser.profile(), otherUser.tokens().access(), otherUser.tokens().refresh(), true).toJson();

        open("/favicon.svg");
        localStorage().clear();
        localStorage().setItem("book_club_auth", localStorageAuthBody);

        clubPage.openClub(createdClub.id());

        step("Проверяем автора и данные отзыва", () -> {
            reviewPage.checkReview(username, review, assessment, readPages);
        });

        step("Проверяем отсутствие действий с чужим отзывом", () -> {
            reviewPage.checkNoEditOrDelete(username);
        });
    }
}
