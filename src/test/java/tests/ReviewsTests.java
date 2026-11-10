package tests;

import helpers.TestUser;
import models.reviews.BlankFieldsReviewResponseModel;
import models.reviews.NotOwnerResponseModel;
import models.reviews.UnauthorizedUserReviewResponseModel;
import models.reviews.ReviewsListResponseModel;
import models.reviews.ReviewResponseModel;
import models.club.ClubResponseModel;
import models.reviews.ReviewBodyModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Map;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.ReviewsSpec.successfulReviewsListResponseSpec;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.ReviewsSpec.successfulReviewResponseSpec;

public class ReviewsTests extends TestBase {
    private TestUser author;
    private ClubResponseModel club;
    private ReviewBodyModel body;

    @BeforeEach
    public void prepareReviewContext() {
        author = createUser();
        club = api.clubs.create(clubData(), author.accessToken());
        body = new ReviewBodyModel(club.id(), "Отзыв " + testData.bookTitle, 4, 120);
    }

    @Test
    @DisplayName("[API] Создание отзыва и доступ к его чтению без авторизации")
    public void createAndReadReview() {
        ReviewResponseModel created = api.reviews.createReview(author.accessToken(), body);

        step("Проверяем данные созданного отзыва", () -> {
            assertThat(created.id()).isPositive();
            assertThat(created.club()).isEqualTo(body.club());
            assertThat(created.user().id()).isEqualTo(author.profile().id());
            assertThat(created.user().username()).isEqualTo(author.username());
            assertThat(created.review()).isEqualTo(body.review());
            assertThat(created.assessment()).isEqualTo(body.assessment());
            assertThat(created.readPages()).isEqualTo(body.readPages());
            assertThat(created.created()).isNotBlank();
            assertThat(created.modified()).isNull();
        });

        ReviewResponseModel read = api.reviews.getReview(created.id());

        step("Проверяем чтение отзыва без авторизации", () -> {
            assertThat(read).isEqualTo(created);
        });
    }

    @Test
    @DisplayName("[API] Полное обновление отзыва через PUT")
    public void updateOwnReviewWithPut() {
        ReviewResponseModel created = api.reviews.createReview(author.accessToken(), body);
        ReviewBodyModel updateBody = new ReviewBodyModel(
                club.id(),
                "Обновлённый отзыв",
                5,
                250
        );

        ReviewResponseModel updated = api.reviews.updateReview(author.accessToken(), created.id(), updateBody);

        step("Проверяем полное обновление отзыва", () -> {
            assertThat(updated.club()).isEqualTo(updateBody.club());
            assertThat(updated.user()).isEqualTo(created.user());
            assertThat(updated.review()).isEqualTo(updateBody.review());
            assertThat(updated.assessment()).isEqualTo(updateBody.assessment());
            assertThat(updated.readPages()).isEqualTo(updateBody.readPages());
            assertThat(updated.id()).isEqualTo(created.id());
            assertThat(updated.created()).isEqualTo(created.created());
            assertThat(updated.modified()).isNotBlank();
            assertThat(api.reviews.getReview(created.id())).isEqualTo(updated);
        });
    }

    @Test
    @DisplayName("[API] Частичное обновление отзыва через PATCH")
    public void updateOwnReviewWithPatch() {
        ReviewResponseModel created = api.reviews.createReview(author.accessToken(), body);
        String updatedText = "Обновлённый отзыв";

        ReviewResponseModel updated = given(baseRequestSpec)
                .header("Authorization", "Bearer " + author.accessToken())
                .body(Map.of("review", updatedText))
                .when()
                .patch("/clubs/reviews/{id}/", created.id())
                .then()
                .spec(successfulReviewResponseSpec)
                .extract()
                .as(ReviewResponseModel.class);

        step("Проверяем изменение текста и сохранение остальных полей", () -> {
            assertThat(updated.review()).isEqualTo(updatedText);
            assertThat(updated.assessment()).isEqualTo(created.assessment());
            assertThat(updated.readPages()).isEqualTo(created.readPages());
            assertThat(updated.club()).isEqualTo(created.club());
            assertThat(updated.user()).isEqualTo(created.user());
            assertThat(updated.id()).isEqualTo(created.id());
            assertThat(updated.created()).isEqualTo(created.created());
            assertThat(updated.modified()).isNotBlank();
            assertThat(api.reviews.getReview(created.id())).isEqualTo(updated);
        });
    }

    @Test
    @DisplayName("[API] Недоступность отзыва после его удаления")
    public void deleteOwnReview() {
        ReviewResponseModel created = api.reviews.createReview(author.accessToken(), body);
        api.reviews.deleteReview(author.accessToken(), created.id());

        String detail = given(baseRequestSpec)
                .get("/clubs/reviews/{id}/", created.id())
                .then()
                .statusCode(404)
                .extract()
                .path("detail");
        ReviewsListResponseModel reviews = given(baseRequestSpec)
                .queryParam("club", club.id())
                .get("/clubs/reviews/")
                .then()
                .spec(successfulReviewsListResponseSpec)
                .extract()
                .as(ReviewsListResponseModel.class);

        step("Проверяем удаление отзыва", () -> {
            assertThat(detail).isEqualTo("No BookReview matches the given query.");
            assertThat(reviews.results()).isEmpty();
        });
    }

    @Test
    @DisplayName("[API] Пользователь не может удалить отзыв другого автора")
    public void anotherUserCannotDeleteReview() {
        ReviewResponseModel created = api.reviews.createReview(author.accessToken(), body);
        TestUser otherUser = createUser();

        NotOwnerResponseModel response = api.reviews.deleteNotOwnerReview(otherUser.accessToken(), created.id());

        step("Проверяем отказ в удалении чужого отзыва", () -> {
            assertThat(response.detail()).isEqualTo("You do not have permission to perform this action.");
        });

        step("Проверяем, что отзыв не удалён и его данные сохранены", () -> {
            assertThat(api.reviews.getReview(created.id())).isEqualTo(created);
        });
    }

    @Test
    @DisplayName("[API] Создание отзыва без авторизации запрещено")
    public void anonymousCannotCreateReview() {
        UnauthorizedUserReviewResponseModel response = api.reviews.createReviewWithoutAuthorization(body);
        ReviewsListResponseModel reviews = given(baseRequestSpec)
                .queryParam("club", club.id())
                .get("/clubs/reviews/")
                .then()
                .spec(successfulReviewsListResponseSpec)
                .extract()
                .as(ReviewsListResponseModel.class);

        step("Проверяем запрет создания отзыва без авторизации", () -> {
            assertThat(response.detail()).isEqualTo("Authentication credentials were not provided.");
            assertThat(reviews.results()).isEmpty();
        });
    }

    @ParameterizedTest(name = "Невалидная оценка: {0}")
    @ValueSource(ints = {0, 6})
    @DisplayName("[API] Оценка ревью должна быть от 1 до 5")
    public void invalidAssessment(int assessment) {
        ReviewBodyModel invalidBody = new ReviewBodyModel(club.id(), body.review(), assessment, 10);
        BlankFieldsReviewResponseModel response = api.reviews.createReviewWithBlankFields(
                author.accessToken(), invalidBody
        );
        ReviewsListResponseModel reviews = given(baseRequestSpec)
                .queryParam("club", club.id())
                .get("/clubs/reviews/")
                .then()
                .spec(successfulReviewsListResponseSpec)
                .extract()
                .as(ReviewsListResponseModel.class);

        step("Проверяем ошибку невалидной оценки", () -> {
            assertThat(response.assessment()).isNotEmpty();
            assertThat(reviews.results()).isEmpty();
        });
    }
}
