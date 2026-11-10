package tests.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import tests.TestBase;
import java.util.Map;
import static com.codeborne.selenide.Selenide.closeWebDriver;

public class UiTestBase extends TestBase {
    @BeforeAll
    static void configureBrowser() {
        Configuration.baseUrl = System.getProperty("baseUrl", "https://book-club.qa.guru");
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.browserVersion = System.getProperty("browserVersion", "");
        Configuration.browserSize = System.getProperty("browserSize", "1920x1080");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        Configuration.timeout = Long.parseLong(System.getProperty("uiTimeout", "10000"));
        String remoteUrl = System.getProperty("remoteUrl", "");
        Configuration.remote = remoteUrl.isBlank() ? null : remoteUrl;
        if (Configuration.remote != null && Configuration.browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-dev-shm-usage", "--no-sandbox");
            options.setCapability("selenoid:options", Map.of("enableVNC", true,
                    "enableVideo", !System.getProperty("videoBaseUrl", "").isBlank()));
            Configuration.browserCapabilities = options;
        }
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(false).savePageSource(false));
    }

    @AfterEach
    void attachAndCloseBrowser() {
        try { Attach.browserArtifacts(); }
        finally { closeWebDriver(); }
    }
}
