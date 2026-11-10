package tests;

import api.ApiClient;
import helpers.TestUser;
import models.club.ClubBodyModel;
import models.login.LoginBodyModel;
import models.login.LoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.RegistrationResponseModel;
import java.util.UUID;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {
    protected final ApiClient api = new ApiClient();
    protected TestData testData;

    @BeforeEach
    public void initTestData() {
        testData = new TestData();
    }

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = System.getProperty("baseUrl", "https://book-club.qa.guru");
    }
    protected TestUser createUser() {
        String username = "autotest_" + UUID.randomUUID().toString().replace("-", "");
        String password = "TestPassword123!";

        RegistrationResponseModel registrationResponse = api.registration.register(
                new RegistrationBodyModel(username, password)
        );
        LoginResponseModel loginResponse = api.auth.login(new LoginBodyModel(username, password));
        return new TestUser(username, loginResponse, registrationResponse);
    }

    protected ClubBodyModel clubData() {
        return new ClubBodyModel(
                testData.bookTitle,
                testData.bookAuthors,
                testData.publicationYear,
                testData.description,
                testData.TELEGRAM_CHAT_LINK
        );
    }
}
