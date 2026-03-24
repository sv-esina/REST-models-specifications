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

public class UpdateUserTests extends TestBase {

    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String emptyUsername = EMPTY_STRING;



    @BeforeEach
    public void prepareTestData() {
        username = randomUsername();
        password = randomPassword();
        firstName = randomFirstName();
        lastName = randomLastName();
        email = randomEmail();
    }

    @Test
    @DisplayName("Успешный update данных с помощью метода PUT/api/v1/users/me/")
    public void successfulPutUpdateUserTest() {

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


        SuccessfulUpdateUserResponseModel updateResponse =
                step("Отправка запроса PUT на update данных пользователя", () -> {
                    UpdateBodyModel updateData = new UpdateBodyModel(username, firstName, lastName, email);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(updateData)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(successfulUpdateResponseSpec)
                            .extract()
                            .as(SuccessfulUpdateUserResponseModel.class);
                });

                step("Проверка обновленных данных", () -> {
                    assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
                    assertThat(updateResponse.username()).isEqualTo(username);
                    assertThat(updateResponse.firstName()).isEqualTo(firstName);
                    assertThat(updateResponse.lastName()).isEqualTo(lastName);
                    assertThat(updateResponse.email()).isEqualTo(email);
                    assertThat(registrationResponse.remoteAddr()).matches(ipAddrRegexp);

                    String updateIpAddress = updateResponse.remoteAddr();
                    assertThat(registrationResponse.remoteAddr()).isEqualTo(updateIpAddress);
                });
    }

    @Test
    @DisplayName("Update данных с отсутствием обязательных параметров (PUT)")
    public void wrongParamsPutUpdateUserTest() {

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


        WrongParamsUpdateUserResponseModel updateResponse =
                step("Отправка запроса PUT с одним параметром username", () -> {
                    WrongParamsUpdateBodyModel wrongParamsUpdateData = new WrongParamsUpdateBodyModel(username);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(wrongParamsUpdateData)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(wrongParamsUpdateUserSpec)
                            .extract()
                            .as(WrongParamsUpdateUserResponseModel.class);
                });

                step("Проверка текста полученных ошибок", () -> {
                    assertThat(updateResponse.firstName().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                    assertThat(updateResponse.lastName().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                    assertThat(updateResponse.email().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
                });
    }

    @Test
    @DisplayName("Update данных с пустым username (PUT)")
    public void emptyUsernamePutUpdateUserTest() {

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


        EmptyUsernameUpdateUserResponseModel updateResponse =
                step("Отправка запроса PUT с пустым параметром username", () -> {
                    UpdateBodyModel updateData = new UpdateBodyModel(emptyUsername, firstName, lastName, email);
                    return given(baseRequestSpec)
                            .header("Authorization", "Bearer " + loginResponse.access())
                            .body(updateData)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(emptyUsernameUpdateUserSpec)
                            .extract()
                            .as(EmptyUsernameUpdateUserResponseModel.class);
                });

                step("Проверка текста полученной ошибки", () -> {
                    assertThat(updateResponse.username().get(0)).isEqualTo(ERROR_BLANK_FIELD);
                });
    }

}

