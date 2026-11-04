package tests;

import api.ApiClient;
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
        RestAssured.baseURI = "https://book-club.qa.guru";
    }
}
