package tests;

import models.registration.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
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

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

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

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel firstRegistrationResponse = api.users.register(registrationData);

        step("Проверка полученного ответа", () -> {
            assertThat(firstRegistrationResponse.username()).isEqualTo(username);
        });

        ExistingUserResponseModel secondRegistrationResponse = api.users.registerExistingUser(registrationData);

        step("Проверка текста ошибки", () -> {
            assertThat(secondRegistrationResponse.username().get(0)).isEqualTo(ERROR_EXISTING_USER);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c пустыми параметрами")
    public void emptyUsernameAndPasswordRegistrationTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(emptyUsername, emptyPassword);

        EmptyParamsRegistrationResponseModel registrationResponse = api.users.setEmptyParamsRegister(registrationData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo(ERROR_BLANK_FIELD);
            assertThat(registrationResponse.password().get(0)).isEqualTo(ERROR_BLANK_FIELD);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя с пустым Request body")
    public void emptyRequestBodyRegistrationTest() {

        WrongRegistrationBodyModel wrongRegistrationData = new WrongRegistrationBodyModel();

        EmptyBodyRegistrationResponseModel registrationResponse = api.users.setEmptyBodyRegister(wrongRegistrationData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(registrationResponse.password().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя с недопустимым символом в username")
    public void invalidUsernameRegistrationTest() {

        RegistrationBodyModel invalidRegistrationData = new RegistrationBodyModel(invalidUsername, password);

        InvalidUsernameUserResponseModel registrationResponse = api.users.setInvalidUsernameRegister(invalidRegistrationData);

        step("Проверка текста полученной ошибки", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo(ERROR_INVALID_USERNAME);
        });
    }

    @Test
    @DisplayName("Регистрация пользователя c null в параметрах")
    public void nullUsernameAndPasswordRegistrationTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(nullUsername, nullPassword);

        EmptyParamsRegistrationResponseModel registrationResponse = api.users.setNullParamsRegister(registrationData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(registrationResponse.username().get(0)).isEqualTo(ERROR_NULL_FIELD);
            assertThat(registrationResponse.password().get(0)).isEqualTo(ERROR_NULL_FIELD);
        });
    }
}