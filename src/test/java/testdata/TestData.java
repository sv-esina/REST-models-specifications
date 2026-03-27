package testdata;

import net.datafaker.Faker;

public class TestData {
    public static final Faker FAKER = new Faker();

    public static final String VALID_USERNAME = "sv_esina";
    public static final String VALID_PASSWORD = "password1";
    public static final String WRONG_PASSWORD = "pass123";
    public static final String WRONG_REFRESH_TOKEN = "wrongToken123";

    public static final String EMPTY_STRING = "";
    public static final String JWT_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String ERROR_REQUIRED_FIELD = "This field is required.";
    public static final String ERROR_BLANK_FIELD = "This field may not be blank.";
    public static final String ERROR_NULL_FIELD = "This field may not be null.";
    public static final String ERROR_INVALID_TOKEN = "Token is invalid";
    public static final String ERROR_TOKEN_NOT_VALID_CODE = "token_not_valid";
    public static final String ERROR_EXISTING_USER = "A user with that username already exists.";
    public static final String ERROR_INVALID_USERNAME =
            "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.";
    public static final String ERROR_CLUB_MATCHES = "No Club matches the given query.";

    public static final String IP_ADDRESS_REGEXP = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
            + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static final String TELEGRAM_LINK = "https://t.me/+WGGl87MjvD9iMzQy";

    private TestData() {
    }

    public static String randomUsername() {
        long randomNumber = FAKER.number().randomNumber(5);
        return FAKER.name().firstName() + randomNumber;
    }

    public static String randomPassword() {
        long randomNumber = FAKER.number().randomNumber(5);
        return FAKER.name().firstName() + randomNumber;
    }

    public static String randomInvalidUsername() {
        return FAKER.regexify("[\\$#%]{5}");
    }

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    public static String randomLastName() {
        return FAKER.name().lastName();
    }

    public static String randomEmail() {
        return FAKER.internet().emailAddress();
    }

    public static String randomBookTitle() { return FAKER.book().title(); }

    public static String randomBookAuthors() { return FAKER.book().author(); }

//    public static Integer randomBookAuthors() { return FAKER.number().numberBetween(1700, 2025); }
}
