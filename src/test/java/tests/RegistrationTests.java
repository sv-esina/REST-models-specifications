package tests;

import models.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.*;
import static testdata.TestData.*;

public class RegistrationTests extends TestBase {

    String username;
    String password;
    String emptyUsername = EMPTY_STRING;
    String emptyPassword = EMPTY_STRING;
    String invalidUsername;
    String nullUsername = null;
    String nullPassword = null;

    @BeforeEach
    public void prepareTestData() {
        username = randomUsername();
        password = randomPassword();
        invalidUsername = randomInvalidUsername();
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    public void successfulRegistrationUserTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Отправка запроса на регистрацию пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(baseRequestSpec)
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

                    assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
                });
    }

    @Test
    @DisplayName("Получение ошибки при попытке повторной регистрации пользователя")
    public void existingUserWrongRegistrationTest() {

        SuccessfulRegistrationResponseModel firstRegistrationResponse =
                step("Успешная регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);
                });

                step("Проверка полученного ответа", () -> {
                    assertThat(firstRegistrationResponse.username()).isEqualTo(username);
                });

        ExistingUserResponseModel secondRegistrationResponse =
                step("Повторная регистрация пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(wrongCredentialsRegisterResponseSpec)
                            .extract().as(ExistingUserResponseModel.class);
                });

                step("Проверка текста ошибки", () -> {
                    assertThat(firstRegistrationResponse.username()).isEqualTo(username);
                    assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(ERROR_EXISTING_USER);
                });
    }

    @Test
    @DisplayName("Регистрация пользователя c пустыми параметрами")
    public void emptyUsernameAndPasswordRegistrationTest() {

        EmptyParamsRegistrationResponseModel RegistrationResponse =
                step("Отправка запроса на регистрацию с пустыми параметрами", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(emptyUsername, emptyPassword);
                    return given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(wrongParamsRegistrationResponseSpec)
                            .extract()
                            .as(EmptyParamsRegistrationResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(RegistrationResponse.username().get(0)).isEqualTo(ERROR_BLANK_FIELD);
                    assertThat(RegistrationResponse.password().get(0)).isEqualTo(ERROR_BLANK_FIELD);
                });
    }

    @Test
    @DisplayName("Регистрация пользователя с пустым Request body")
    public void emptyRequestBodyRegistrationTest() {

        EmptyParamsRegistrationResponseModel RegistrationResponse =
                step("Отправка запроса на регистрацию с пустым Request body", () -> {
                    WrongRegistrationBodyModel wrongRegistrationData = new WrongRegistrationBodyModel();
                    return given(baseRequestSpec)
                            .body(wrongRegistrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(wrongParamsRegistrationResponseSpec)
                            .extract()
                            .as(EmptyParamsRegistrationResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(RegistrationResponse.username().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                    assertThat(RegistrationResponse.password().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                });
    }

    @Test
    @DisplayName("Регистрация пользователя с недопустимым символом в username")
    public void invalidUsernameRegistrationTest() {

        EmptyParamsRegistrationResponseModel RegistrationResponse =
                step("Отправка запроса на регистрацию с невалидным username", () -> {
                    RegistrationBodyModel wrongRegistrationData = new RegistrationBodyModel(invalidUsername, password);
                    return given(baseRequestSpec)
                            .body(wrongRegistrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(wrongCredentialsRegisterResponseSpec)
                            .extract()
                            .as(EmptyParamsRegistrationResponseModel.class);
                });

                step("Проверка текста полученной ошибки", () -> {
                    assertThat(RegistrationResponse.username().get(0)).isEqualTo(ERROR_INVALID_USERNAME);
                });
    }

    @Test
    @DisplayName("Регистрация пользователя c null в параметрах")
    public void nullUsernameAndPasswordRegistrationTest() {

        EmptyParamsRegistrationResponseModel RegistrationResponse =
                step("Отправка null строк для регистрации", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(nullUsername, nullPassword);
                    return given(baseRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(wrongParamsRegistrationResponseSpec)
                            .extract()
                            .as(EmptyParamsRegistrationResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(RegistrationResponse.username().get(0)).isEqualTo(ERROR_NULL_FIELD);
                    assertThat(RegistrationResponse.password().get(0)).isEqualTo(ERROR_NULL_FIELD);
                });
    }
}