package pages;

import io.qameta.allure.Step;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ClubsPage {
    @Step("Открыть список клубов")
    public ClubsPage openPage() {
        open("/");
        $(".search-input").shouldBe(visible);
        return this;
    }

    @Step("Найти клуб по уникальному названию")
    public ClubsPage searchClub(String title) {
        $(".search-input").setValue(title);
        $(".search-button").click();
        return this;
    }

    @Step("Проверить карточку клуба")
    public ClubsPage checkClub(String title, String author) {
        $$(".club-card").findBy(text(title)).shouldBe(visible)
                .$(".authors").shouldHave(text(author));
        return this;
    }

    public void checkExistClub(String title) {
        $$(".club-card").findBy(text(title)).shouldBe(visible);
    }

    public void checkCreatedClubData(String title, String year, String authors, String description) {
        com.codeborne.selenide.SelenideElement card = $$(".club-card").findBy(text(title));
        card.$("h2").shouldHave(exactText(title));
        card.$(".year").shouldHave(exactText(year));
        card.$(".authors").shouldHave(exactText(authors));
        card.$(".description").shouldHave(exactText(description));
    }

    @Step("Проверить отсутствие клуба в поиске")
    public ClubsPage checkNoResults() {
        $(".no-results").shouldHave(text("Клубы не найдены"));
        $$(".club-card").shouldHave(com.codeborne.selenide.CollectionCondition.size(0));
        return this;
    }
}
