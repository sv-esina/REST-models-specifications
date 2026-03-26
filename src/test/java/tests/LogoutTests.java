package tests;

import models.login.LoginBodyModel;
import models.logout.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testdata.TestData.*;

public class LogoutTests extends TestBase{

    String emptyRefreshToken = EMPTY_STRING;
    String nullRefreshToken = null;

    @Test
    @DisplayName("Успешный выход из сессии")
    public void successfulLogoutTest() {

        LoginBodyModel loginData = new LoginBodyModel(VALID_USERNAME, VALID_PASSWORD);

        String getRefreshToken = api.auth.getRefreshToken(loginData);

        LogoutBodyModel logoutData = new LogoutBodyModel(getRefreshToken);
        api.auth.logout(logoutData);

    }

    @Test
    @DisplayName("Выполнение запроса с некорректным токеном")
    public void invalidTokenLogoutTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(WRONG_REFRESH_TOKEN);

        InvalidTokenLogoutModel logoutResponse = api.auth.setInvalidRefreshToken(logoutData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(logoutResponse.detail()).isEqualTo(ERROR_INVALID_TOKEN);
            assertThat(logoutResponse.code()).isEqualTo(ERROR_TOKEN_NOT_VALID_CODE);
        });
    }

    @Test
    @DisplayName("Выполнение запроса с null в параметре refresh")
    public void nullTokenLogoutTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(nullRefreshToken);

        EmptyOrNullParamLogoutResponseModel logoutResponse = api.auth.setNullOrEmptyRefreshToken(logoutData);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(logoutResponse.refresh().get(0)).isEqualTo(ERROR_NULL_FIELD);
        });
    }

    @Test
    @DisplayName("Выполнение запроса с пустым параметром refresh")
    public void emptyTokenLogoutTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(emptyRefreshToken);

        EmptyOrNullParamLogoutResponseModel logoutResponse = api.auth.setNullOrEmptyRefreshToken(logoutData);

        step("Проверка текста полученной ошибки", () -> {
            assertThat(logoutResponse.refresh().get(0)).isEqualTo(ERROR_BLANK_FIELD);
        });
    }
}
