package helpers;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.ByteArrayInputStream;
import static com.codeborne.selenide.Selenide.getWebDriverLogs;

public class Attach {
    public static void browserArtifacts() {
        if (!WebDriverRunner.hasWebDriverStarted()) return;
        capture("Screenshot", () -> Allure.addAttachment("Скриншот", "image/png",
                new ByteArrayInputStream(((TakesScreenshot) WebDriverRunner.getWebDriver())
                        .getScreenshotAs(OutputType.BYTES)), ".png"));
        capture("Page source", () -> Allure.addAttachment("HTML страницы", "text/html",
                WebDriverRunner.getWebDriver().getPageSource(), ".html"));
        capture("Browser logs", () -> Allure.addAttachment("Консоль браузера", "text/plain",
                String.join("\n", getWebDriverLogs("browser"))));
        String videoBase = System.getProperty("videoBaseUrl", "");
        if (!videoBase.isBlank() && WebDriverRunner.getWebDriver() instanceof RemoteWebDriver driver) {
            String video = videoBase.replaceAll("/+$", "") + "/" + driver.getSessionId() + ".mp4";
            Allure.addAttachment("Видео сессии", "text/uri-list", video, ".uri");
        }
    }

    private static void capture(String name, Runnable action) {
        try { action.run(); }
        catch (RuntimeException e) { Allure.addAttachment(name + " unavailable", e.toString()); }
    }
}
