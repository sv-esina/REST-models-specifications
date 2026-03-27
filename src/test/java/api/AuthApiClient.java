package api;

import io.qameta.allure.Step;
import models.login.*;
import models.logout.EmptyOrNullParamLogoutResponseModel;
import models.logout.InvalidTokenLogoutModel;
import models.logout.LogoutBodyModel;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.*;
import static specs.logout.LogoutSpec.*;


public class AuthApiClient {

    @Step("Ввод правильного логина и правильного пароля")
    public SuccessfulLoginResponseModel login (LoginBodyModel loginData) {
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);
    }

    @Step("Отправка завпроса auth с некорректным паролем")
    public WrongPasswordLoginResponseModel wrongPassword (LoginBodyModel loginData) {
         return given(baseRequestSpec)
                        .body(loginData)
                        .when()
                        .post("/auth/token/")
                        .then()
                        .spec(wrongPasswordLoginResponseSpec)
                        .extract().as(WrongPasswordLoginResponseModel.class);
    }

    @Step("Отправка завпроса auth с пустыми параметрами")
    public EmptyParamsLoginResponseModel emptyParams (LoginBodyModel loginData){
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract().as(EmptyParamsLoginResponseModel.class);
    }

    @Step("Отправка завпроса auth с пустым body")
    public EmptyBodyLoginResponseModel emptyBody (WrongLoginBodyModel wrongLoginData){
        return given(baseRequestSpec)
                .body(wrongLoginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract().as(EmptyBodyLoginResponseModel.class);
    }

    @Step("Отправка завпроса auth с null в параметрах")
    public NullParamsLoginResponseModel nullParams (LoginBodyModel loginData){
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(emptyPasswordLoginResponseSpec)
                .extract().as(NullParamsLoginResponseModel.class);
    }

    @Step("Авторизация и получение refresh токена")
    public String getRefreshToken (LoginBodyModel loginData){
        return given(baseRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().path("refresh");
    }

    @Step("Отправка запроса logout")
    public void logout (LogoutBodyModel logoutData){
             given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec);
    }

    @Step("Отправка завпроса logout с некорректным токеном")
    public InvalidTokenLogoutModel setInvalidRefreshToken (LogoutBodyModel logoutData){
        return given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidLogoutResponseSpec)
                .extract().as(InvalidTokenLogoutModel.class);
    }

    @Step("Отправка завпроса logout с null в параметре refresh")
    public EmptyOrNullParamLogoutResponseModel setNullOrEmptyRefreshToken (LogoutBodyModel logoutData){
        return given(baseRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(wrongLogoutResponseSpec)
                .extract().as(EmptyOrNullParamLogoutResponseModel.class);
    }

}
