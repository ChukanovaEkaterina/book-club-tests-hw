package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ReviewClubPage {
    private SelenideElement card(String username) {
        return $$(".review-card").findBy(text(username));
    }

    @Step("Проверить текст, автора, оценку и страницы ревью")
    public ReviewClubPage checkReview(String username, String review, int assessment, int readPages) {
        var card = card(username).shouldBe(visible);
        card.$(".reviewer-name").shouldHave(exactText(username));
        card.$(".review-content").shouldHave(exactText(review));
        card.$(".stars").shouldHave(exactText("★".repeat(assessment) + "☆".repeat(5 - assessment)));
        card.$(".read-pages").shouldHave(exactText(readPages + " стр."));
        return this;
    }

    @Step("Открыть форму создания отзыва")
    public void clickAddReviewButton() {
        $(".add-review-btn").shouldBe(visible).click();
    }

    @Step("Заполнить оценку")
    public void enterAssessment(int assessment) {
        $("#assessment").setValue(String.valueOf(assessment));
    }

    @Step("Заполнить количество прочитанных страниц")
    public void enterReadPages(int readPages) {
        $("#readPages").setValue(String.valueOf(readPages));
    }

    @Step("Заполнить текст отзыва")
    public void enterReview(String review) {
        $("#review").setValue(review);
    }

    @Step("Сохранить отзыв")
    public void clickSaveReviewButton() {
        $(".review-form .save-btn").click();
        $(".review-form").shouldNot(exist);
    }

    @Step("Изменить свой отзыв через UI")
    public ReviewClubPage edit(String username, String review, int assessment, int readPages) {
        card(username).$(".edit-review-btn").shouldBe(visible).click();
        enterAssessment(assessment);
        enterReadPages(readPages);
        enterReview(review);
        clickSaveReviewButton();
        return this;
    }

    @Step("Удалить своё ревью через UI")
    public ReviewClubPage delete(String username) {
        card(username).$(".delete-review-btn").click();
        confirm();
        card(username).shouldNot(exist);
        return this;
    }

    @Step("Проверить пустой список ревью")
    public ReviewClubPage checkEmpty() {
        $(".no-reviews").shouldHave(text("Пока нет отзывов"));
        $$(".review-card").shouldHave(com.codeborne.selenide.CollectionCondition.size(0));
        return this;
    }

    @Step("Проверить отсутствие действий с чужим ревью")
    public ReviewClubPage checkNoEditOrDelete(String username) {
        card(username).shouldBe(visible).$(".edit-review-btn").shouldNot(exist);
        card(username).$(".delete-review-btn").shouldNot(exist);
        return this;
    }
}
