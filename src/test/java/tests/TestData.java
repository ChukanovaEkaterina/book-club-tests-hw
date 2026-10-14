package tests;

import net.datafaker.Faker;
import java.util.List;
import java.util.Map;

public class TestData {
    private final Faker faker = new Faker();

    public String username = "Chukanova" + faker.name().firstName();
    public String password = faker.name().firstName();
    public String wrongPassword = faker.name().firstName();
    public static final String EMPTY_USERNAME = "";
    public static final String EMPTY_PASSWORD = "";
    public static final String FIXED_TOKEN_PART = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String REGISTERED_USERNAME = "Chukanova123";
    public static final String REGISTERED_PASSWORD = "Chukanova123";
    public static final String INVALID_REFRESH_TOKEN = "invalid_token";
    public String updateFirstName = faker.name().firstName();
    public String updateLastName = faker.name().lastName();
    public String updateEmail = faker.internet().emailAddress();
}
