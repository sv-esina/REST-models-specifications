package tests;

import models.registration.*;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.RequestSpec.requestSpec;
import static specs.registration.ResponseRegistrationSpec.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;
    String emptyUsername = "";
    String emptyPassword = "";
    String invalidUsername;
    String nullUsername = null;
    String nullPassword = null;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        invalidUsername = faker.regexify("[\\$#%]{5}");
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void successfulRegistrationUserTest() {

        SuccessfulRegistrationResponseModel
                registrationResponse = step("Отправка запроса на регистрацию пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);
        });

        step("Проверка получения ответа с кодом 201", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");

            String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
            assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);
        });
    }

    @Test
    @DisplayName("Получение ошибки при попытке повторной регистрации пользователя")
    public void existingUserWrongRegistrationTest() {

        SuccessfulRegistrationResponseModel
                firstRegistrationResponse = step("Успешная регистрация нового пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(successRegistrationResponseSpec)
                    .extract().as(SuccessfulRegistrationResponseModel.class);
        });
        assertThat(firstRegistrationResponse.username()).isEqualTo(username);

        ExistingUserResponseModel
                secondRegistrationResponse = step("Повторная регистрация пользователя", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongCredentialsRegisterResponseSpec)
                    .extract().as(ExistingUserResponseModel.class);
        });

        step("Проверка текста ошибки", () -> {
            assertThat(firstRegistrationResponse.username()).isEqualTo(username);
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo("A user with that username already exists.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c пустыми параметрами")
    public void emptyUsernameAndPasswordRegistrationTest() {

        EmptyParamsRegistrationResponseModel
                RegistrationResponse = step("Отправка запроса на регистрацию с пустыми параметрами", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(emptyUsername, emptyPassword);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongParamsRegistrationResponseSpec)
                    .extract()
                    .as(EmptyParamsRegistrationResponseModel.class);
        });

        step("Проверка текста полученных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo("This field may not be blank.");
            assertThat(RegistrationResponse.password().get(0)).isEqualTo("This field may not be blank.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя с пустым Request body")
    public void emptyRequestBodyRegistrationTest() {

        EmptyParamsRegistrationResponseModel
                RegistrationResponse = step("Отправка запроса на регистрацию с пустым Request body", () -> {
            WrongRegistrationBodyModel wrongRegistrationData = new WrongRegistrationBodyModel();
            return given(requestSpec)
                    .body(wrongRegistrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongParamsRegistrationResponseSpec)
                    .extract()
                    .as(EmptyParamsRegistrationResponseModel.class);
        });

        step("Проверка текста полученных ошибок", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo("This field is required.");
            assertThat(RegistrationResponse.password().get(0)).isEqualTo("This field is required.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя с недопустимым символом в username")
    public void invalidUsernameRegistrationTest() {

        EmptyParamsRegistrationResponseModel
                RegistrationResponse = step("Отправка запроса на регистрацию с невалидным username", () -> {
            RegistrationBodyModel wrongRegistrationData = new RegistrationBodyModel(invalidUsername, password);
            return given(requestSpec)
                    .body(wrongRegistrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongCredentialsRegisterResponseSpec)
                    .extract()
                    .as(EmptyParamsRegistrationResponseModel.class);
        });

        step("Проверка текста полученной ошибки", () -> {
            assertThat(RegistrationResponse.username().get(0)).isEqualTo("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.");
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c null в параметрах")
    public void nullUsernameAndPasswordRegistrationTest() {

        EmptyParamsRegistrationResponseModel registrationResponse = step("Отправка null строк для регистрации", () -> {
            RegistrationBodyModel registrationData = new RegistrationBodyModel(nullUsername, nullPassword);
            return given(requestSpec)
                    .body(registrationData)
                    .when()
                    .post("/users/register/")
                    .then()
                    .spec(wrongParamsRegistrationResponseSpec)
                    .extract()
                    .as(EmptyParamsRegistrationResponseModel.class);
        });

        step("Проверка текста полученных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo("This field may not be null.");
            assertThat(registrationResponse.password().get(0)).isEqualTo("This field may not be null.");
        });
    }
}