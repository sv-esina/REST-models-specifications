package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.BaseSpec.baseRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.successRegistrationResponseSpec;
import static specs.update.UpdateUserSpec.*;
import static testdata.TestData.*;

public class PatchUpdateUserTests extends TestBase {

    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String newUsername;
    String invalidUsername;



    @BeforeEach
    public void prepareTestData() {
        username = randomUsername();
        newUsername = randomUsername();
        password = randomPassword();
        firstName = randomFirstName();
        lastName = randomLastName();
        email = randomEmail();
        invalidUsername = randomInvalidUsername();
    }

    @Test
    @DisplayName("Успешный update всех данных с помощью метода PATCH /api/v1/users/me/")
    public void successfulPatchUpdateUserTest() {

        String ipAddrRegexp = IP_ADDRESS_REGEXP;

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация пользователя", () -> {
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
                    assertThat(registrationResponse.id()).isGreaterThan(0);
                    assertThat(registrationResponse.username()).isEqualTo(username);
                    assertThat(registrationResponse.firstName()).isEqualTo("");
                    assertThat(registrationResponse.lastName()).isEqualTo("");
                    assertThat(registrationResponse.email()).isEqualTo("");
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                });

        SuccessfulLoginResponseModel loginResponse =
                step("Авторизация зарегистрированного пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class);
                });

                step("Проверка полученных токенов", () -> {
                    String actualAccessToken = loginResponse.access();
                    String actualRefreshToken = loginResponse.refresh();

                    assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);
                });


        SuccessfulPatchUpdateUserResponseModel updateResponse =
                step("Отправка запроса PATCH на update всех данных пользователя", () -> {
                    PatchUpdateBodyModel patchUpdateData = new PatchUpdateBodyModel(newUsername, firstName, lastName, email);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(patchUpdateData)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(successfulPatchUserUpdateSpec)
                            .extract()
                            .as(SuccessfulPatchUpdateUserResponseModel.class);
                });

                step("Проверка обновленных данных", () -> {
                    assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
                    assertThat(updateResponse.username()).isEqualTo(newUsername);
                    assertThat(updateResponse.firstName()).isEqualTo(firstName);
                    assertThat(updateResponse.lastName()).isEqualTo(lastName);
                    assertThat(updateResponse.email()).isEqualTo(email);
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                    String updateIpAddress = updateResponse.remoteAddr();
                    assertThat(registrationResponse.remoteAddr()).isEqualTo(updateIpAddress);
                });
    }

    @Test
    @DisplayName("Успешный update только параметра username с помощью метода PATCH /api/v1/users/me/")
    public void successfulPatchUpdateOnlyUsernameParamTest() {

        String ipAddrRegexp = IP_ADDRESS_REGEXP;

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация пользователя", () -> {
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
                    assertThat(registrationResponse.id()).isGreaterThan(0);
                    assertThat(registrationResponse.username()).isEqualTo(username);
                    assertThat(registrationResponse.firstName()).isEqualTo("");
                    assertThat(registrationResponse.lastName()).isEqualTo("");
                    assertThat(registrationResponse.email()).isEqualTo("");
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                });

        SuccessfulLoginResponseModel loginResponse =
                step("Авторизация зарегистрированного пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class);
                });

                step("Проверка полученных токенов", () -> {
                    String actualAccessToken = loginResponse.access();
                    String actualRefreshToken = loginResponse.refresh();

                    assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);
                });


        SuccessfulPatchUpdateUserResponseModel updateResponse =
                step("Отправка запроса PATCH с заменой одного параметра username", () -> {
                    PatchUsernameParamUpdateBodyModel patchUpdateData = new PatchUsernameParamUpdateBodyModel(newUsername);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(patchUpdateData)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(successfulOneParamPatchUserUpdateSpec)
                            .extract()
                            .as(SuccessfulPatchUpdateUserResponseModel.class);
                });

                step("Проверка обновленных данных", () -> {
                    assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
                    assertThat(updateResponse.username()).isEqualTo(newUsername);
                    assertThat(updateResponse.firstName()).isEqualTo("");
                    assertThat(updateResponse.lastName()).isEqualTo("");
                    assertThat(updateResponse.email()).isEqualTo("");
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                    String updateIpAddress = updateResponse.remoteAddr();
                    assertThat(registrationResponse.remoteAddr()).isEqualTo(updateIpAddress);
                });
    }


    @Test
    @DisplayName("Успешный update только параметра firstName с помощью метода PATCH /api/v1/users/me/")
    public void successfulPatchUpdateOnlyFirstNameParamTest() {

        String ipAddrRegexp = IP_ADDRESS_REGEXP;

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация пользователя", () -> {
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
                    assertThat(registrationResponse.id()).isGreaterThan(0);
                    assertThat(registrationResponse.username()).isEqualTo(username);
                    assertThat(registrationResponse.firstName()).isEqualTo("");
                    assertThat(registrationResponse.lastName()).isEqualTo("");
                    assertThat(registrationResponse.email()).isEqualTo("");
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                });

        SuccessfulLoginResponseModel loginResponse =
                step("Авторизация зарегистрированного пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class);
                });

                step("Проверка полученных токенов", () -> {
                    String actualAccessToken = loginResponse.access();
                    String actualRefreshToken = loginResponse.refresh();

                    assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);
                });


        SuccessfulPatchUpdateUserResponseModel patchUpdateResponse =
                step("Отправка запроса PATCH на update только параметра firstName", () -> {
                    PatchFirstNameParamUpdateBodyModel patchUpdateData = new PatchFirstNameParamUpdateBodyModel(firstName);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(patchUpdateData)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(successfulOneParamPatchUserUpdateSpec)
                            .extract()
                            .as(SuccessfulPatchUpdateUserResponseModel.class);
                });

                step("Проверка обновленных данных", () -> {
                    assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
                    assertThat(patchUpdateResponse.username()).isEqualTo(username);
                    assertThat(patchUpdateResponse.firstName()).isEqualTo(firstName);
                    assertThat(patchUpdateResponse.lastName()).isEqualTo("");
                    assertThat(patchUpdateResponse.email()).isEqualTo("");
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                    String updateIpAddress = patchUpdateResponse.remoteAddr();
                    assertThat(registrationResponse.remoteAddr()).isEqualTo(updateIpAddress);
                });
    }

    @Test
    @DisplayName("Update пользователя с недопустимым символом в username (PATCH)")
    public void invalidUsernameParamPatchUpdateTest() {

        String ipAddrRegexp = IP_ADDRESS_REGEXP;

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация пользователя", () -> {
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
                    assertThat(registrationResponse.id()).isGreaterThan(0);
                    assertThat(registrationResponse.username()).isEqualTo(username);
                    assertThat(registrationResponse.firstName()).isEqualTo("");
                    assertThat(registrationResponse.lastName()).isEqualTo("");
                    assertThat(registrationResponse.email()).isEqualTo("");
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                });

        SuccessfulLoginResponseModel loginResponse =
                step("Авторизация зарегистрированного пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(baseRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class);
                });

                step("Проверка полученных токенов", () -> {
                    String actualAccessToken = loginResponse.access();
                    String actualRefreshToken = loginResponse.refresh();

                    assertThat(actualAccessToken).isNotEqualTo(actualRefreshToken);
        });


        WrongParamPatchUpdateResponseModel patchUpdateResponse =
                step("Отправка запроса PATCH с невалидным username", () -> {
                    PatchUpdateBodyModel patchUpdateData = new PatchUpdateBodyModel(invalidUsername, firstName, lastName, email);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(patchUpdateData)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(wrongPatchUsernameUpdateUserSpec)
                            .extract()
                            .as(WrongParamPatchUpdateResponseModel.class);
                });

                step("Проверка текста полученной ошибки", () -> {
                    assertThat(patchUpdateResponse.username().get(0)).isEqualTo(ERROR_INVALID_USERNAME);
                });
    }


}

