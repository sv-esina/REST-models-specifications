package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import models.login.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static testdata.TestData.*;

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

        SuccessfulLoginResponseModel loginResponse =
                step("Отправка успешного запроса на авторизацию пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(VALID_USERNAME, VALID_PASSWORD);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class);
                });

                step("Проверка корректности полученых токенов", () -> {
                    String expectedTokenPath = JWT_TOKEN_PREFIX;
                    String actualAccess = loginResponse.access();
                    String actualRefresh = loginResponse.refresh();

                    assertThat(actualAccess).startsWith(expectedTokenPath);
                    assertThat(actualRefresh).startsWith(expectedTokenPath);
                    assertThat(actualAccess).isNotEqualTo(actualRefresh);
                });
    }

    @Test
    @DisplayName("Проверка ввода некорректного пароля")
    public void wrongPasswordLoginTest() {

        WrongPasswordLoginResponseModel loginResponse =
                step("Отправка завпроса с некорректным паролем", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(VALID_USERNAME, WRONG_PASSWORD);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(wrongPasswordLoginResponseSpec)
                            .extract().as(WrongPasswordLoginResponseModel.class);
                });

                step("Проверка текста полученной ошибки", () -> {
                    String expectedDetailError = ERROR_INVALID_CREDENTIALS;
                    String actualDetailError = loginResponse.detail();

                    assertThat(actualDetailError).isEqualTo(expectedDetailError);
                });
    }

    @Test
    @DisplayName("Авторизация незарегистрированным пользователем")
    public void wrongUsernameLoginTest() {

        WrongPasswordLoginResponseModel loginResponse =
                step("Отправка завпроса с незарегистрированным пользователем", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(invalidUsername, VALID_PASSWORD);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(wrongPasswordLoginResponseSpec)
                            .extract().as(WrongPasswordLoginResponseModel.class);
                });

                step("Проверка текста полученной ошибки", () -> {
                    String expectedDetailError = ERROR_INVALID_CREDENTIALS;
                    String actualDetailError = loginResponse.detail();

                    assertThat(actualDetailError).isEqualTo(expectedDetailError);
                });
    }

    @Test
    @DisplayName("Авторизация пользователя c пустыми параметрами")
    public void emptyUsernameAndPasswordLoginTest() {

        EmptyParamsLoginResponseModel loginResponse =
                step("Отправка завпроса с некорректным паролем", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(emptyUsername, emptyPassword);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(emptyPasswordLoginResponseSpec)
                            .extract().as(EmptyParamsLoginResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(loginResponse.username().get(0)).isEqualTo(ERROR_BLANK_FIELD);
                    assertThat(loginResponse.password().get(0)).isEqualTo(ERROR_BLANK_FIELD);
                });
    }

    @Test
    @DisplayName("Авторизация пользователя с пустым Request body")
    public void emptyRequestBodyRegistrationTest() {

        EmptyParamsLoginResponseModel loginResponse =
                step("Отправка запроса на авторизацию с пустым Request body", () -> {
                    WrongLoginBodyModel wrongLoginData = new WrongLoginBodyModel();
                    return given(baseRequestSpec)
                            .body(wrongLoginData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(emptyPasswordLoginResponseSpec)
                            .extract()
                            .as(EmptyParamsLoginResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(loginResponse.username().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                    assertThat(loginResponse.password().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                });
    }

    @Test
    @DisplayName("Авторизация пользователя c null в параметрах")
    public void nullRequestBodyRegistrationTest() {

        EmptyParamsLoginResponseModel loginResponse =
                step("Отправка запроса c null в параметрах ", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(nullUsername, nullPassword);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(emptyPasswordLoginResponseSpec)
                            .extract()
                            .as(EmptyParamsLoginResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(loginResponse.username().get(0)).isEqualTo(ERROR_NULL_FIELD);
                    assertThat(loginResponse.password().get(0)).isEqualTo(ERROR_NULL_FIELD);
                });
    }
}
