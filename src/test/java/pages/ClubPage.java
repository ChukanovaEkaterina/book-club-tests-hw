package pages;

import io.qameta.allure.Step;
import models.club.ClubBodyModel;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ClubPage {
    @Step("Открыть страницу клуба {id}")
    public ClubPage openClub(int id) {
        open("/clubs/" + id);
        $(".club-content").shouldBe(visible);
        return this;
    }

    @Step("Проверить данные клуба на странице")
    public ClubPage checkDetails(ClubBodyModel body) {
        $(".club-header h1").shouldHave(exactText(body.bookTitle()));
        $(".club-header .year").shouldHave(text(String.valueOf(body.publicationYear())));
        $(".club-content > .authors").shouldHave(text(body.bookAuthors()));
        $(".club-content > .description").shouldHave(exactText(body.description()));
        $(".telegram-btn").shouldHave(attribute("href", body.telegramChatLink()));
        return this;
    }

    @Step("Проверить, что редактирование клуба недоступно")
    public ClubPage checkNoEdit() {
        $(".club-actions .edit-btn").shouldNot(exist);
        return this;
    }
}
