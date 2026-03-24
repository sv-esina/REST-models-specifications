package tests;

import models.login.LoginBodyModel;
import models.logout.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static specs.logout.LogoutSpec.*;

public class LogoutTests extends TestBase{
    String username = "sv_esina";
    String password = "password1";
    String wrongRefreshToken = "wrongToken123";
    String emptyRefreshToken = "";
    String nullRefreshToken = null;

    @Test
    @DisplayName("Успешный выход из сессии")
    public void successfulLogoutTest() {

        String refreshToken =
                step("Авторизация и получение refresh токена", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().path("refresh");
                });

                step("Отправка запроса на выход из сессии", () -> {
                    LogoutBodyModel logoutBody = new LogoutBodyModel(refreshToken);
                    SuccessfulLogoutResponseModel successfulLogout = given(baseRequestSpec)
                            .body(logoutBody)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(successfulLogoutResponseSpec)
                            .extract().as(SuccessfulLogoutResponseModel.class);
                });
    }

    @Test
    @DisplayName("Выполнение запроса с некорректным токеном")
    public void invalidTokenLogoutTest() {

        InvalidTokenLogoutModel invalidToken =
                step("Отправка запроса с некорректным токеном", () -> {
                    LogoutBodyModel logoutBody = new LogoutBodyModel(wrongRefreshToken);
                    return given(baseRequestSpec)
                            .body(logoutBody)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(invalidLogoutResponseSpec)
                            .extract().as(InvalidTokenLogoutModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(invalidToken.detail()).isEqualTo("Token is invalid");
                    assertThat(invalidToken.code()).isEqualTo("token_not_valid");
                });
    }

    @Test
    @DisplayName("Выполнение запроса с null в параметре refresh")
    public void nullTokenLogoutTest() {

        EmptyOrNullParamLogoutResponseModel nullOrEmptyToken =
                step("Отправка запроса с некорректным токеном", () -> {
                    LogoutBodyModel logoutBody = new LogoutBodyModel(nullRefreshToken);
                    return given(baseRequestSpec)
                            .body(logoutBody)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(wrongLogoutResponseSpec)
                            .extract().as(EmptyOrNullParamLogoutResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(nullOrEmptyToken.refresh().get(0)).isEqualTo("This field may not be null.");
                });
    }

    @Test
    @DisplayName("Выполнение запроса с пустым параметром refresh")
    public void emptyTokenLogoutTest() {

        EmptyOrNullParamLogoutResponseModel nullOrEmptyToken =
                step("Отправка запроса с пустым параметром refresh", () -> {
                    LogoutBodyModel logoutBody = new LogoutBodyModel(emptyRefreshToken);
                    return given(baseRequestSpec)
                            .body(logoutBody)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(wrongLogoutResponseSpec)
                            .extract().as(EmptyOrNullParamLogoutResponseModel.class);
                });

                step("Проверка текста полученной ошибки", () -> {
                    assertThat(nullOrEmptyToken.refresh().get(0)).isEqualTo("This field may not be blank.");
                });
    }
}
