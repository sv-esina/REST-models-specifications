package tests;

import models.login.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testdata.TestData.*;
import static testdata.TestData.VALID_PASSWORD;

public class LoginTests  extends TestBase {

    String emptyUsername = EMPTY_STRING;
    String emptyPassword = EMPTY_STRING;
    String invalidUsername;
    String nullUsername = null;
    String nullPassword = null;

    @BeforeEach
    public void prepareTestData() {
        invalidUsername = randomUsername();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(VALID_USERNAME, VALID_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        step("Проверка корректности полученых токенов", () -> {
            assertThat(loginResponse.access()).startsWith(JWT_TOKEN_PREFIX);
            assertThat(loginResponse.refresh()).startsWith(JWT_TOKEN_PREFIX);
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });
    }

    @Test
    @DisplayName("Проверка ввода некорректного пароля")
    public void wrongPasswordLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(VALID_USERNAME, WRONG_PASSWORD);

        WrongPasswordLoginResponseModel loginResponse = api.auth.wrongPassword(loginData);

        step("Проверка текста полученной ошибки", () -> {
            assertThat(loginResponse.detail()).isEqualTo(ERROR_INVALID_CREDENTIALS);
        });
    }

    @Test
    @DisplayName("Авторизация незарегистрированным пользователем")
    public void wrongUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(invalidUsername, VALID_PASSWORD);

        WrongPasswordLoginResponseModel loginResponse = api.auth.wrongPassword(loginData);

        step("Проверка текста полученной ошибки", () -> {
            assertThat(loginResponse.detail()).isEqualTo(ERROR_INVALID_CREDENTIALS);
        });
    }

    @Test
    @DisplayName("Авторизация пользователя c пустыми параметрами")
    public void emptyUsernameAndPasswordLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(emptyUsername, emptyPassword);

        EmptyParamsLoginResponseModel loginResponse = api.auth.emptyParams(loginData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(loginResponse.username().get(0)).isEqualTo(ERROR_BLANK_FIELD);
            assertThat(loginResponse.password().get(0)).isEqualTo(ERROR_BLANK_FIELD);
        });
    }

    @Test
    @DisplayName("Авторизация пользователя с пустым Request body")
    public void emptyRequestBodyRegistrationTest() {

        WrongLoginBodyModel wrongLoginData = new WrongLoginBodyModel();

        EmptyBodyLoginResponseModel loginResponse = api.auth.emptyBody(wrongLoginData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(loginResponse.username().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(loginResponse.password().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
        });
    }

    @Test
    @DisplayName("Авторизация пользователя c null в параметрах")
    public void nullRequestBodyRegistrationTest() {

        LoginBodyModel loginData = new LoginBodyModel(nullUsername, nullPassword);

        NullParamsLoginResponseModel loginResponse = api.auth.nullParams(loginData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(loginResponse.username().get(0)).isEqualTo(ERROR_NULL_FIELD);
            assertThat(loginResponse.password().get(0)).isEqualTo(ERROR_NULL_FIELD);
        });
    }
}
