package pages;

import io.qameta.allure.Step;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ClubFormPage {
    @Step("Открыть страницу создания клуба")
    public void openCreateClubPage() {
        open("/clubs/create");
        $("#bookTitle").shouldBe(visible);
    }

    public void enterBookTitle(String title) {
        $("#bookTitle").shouldBe(enabled).setValue(title);
    }

    public void enterBookAuthors(String authors) {
        $("#bookAuthors").setValue(authors);
    }

    public void enterPublicationYear(String year) {
        $("#publicationYear").setValue(year);
    }

    public void enterDescription(String description) {
        $("#description").setValue(description);
    }

    public void enterTelegramChatLink(String link) {
        $("#telegramChatLink").setValue(link);
    }

    @Step("Сохранить клуб")
    public void clickSubmitButton() {
        $(".submit-btn").click();
        $(".search-input").shouldBe(visible);
    }
}
