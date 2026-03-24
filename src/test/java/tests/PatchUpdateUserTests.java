package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.*;
import net.datafaker.Faker;
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

public class PatchUpdateUserTests extends TestBase {

    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String newUsername;
    Long randomNumber;
    String invalidUsername;



    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        randomNumber = faker.number().randomNumber(5);
        username = faker.name().firstName() + randomNumber;
        newUsername = faker.name().firstName() + randomNumber;
        password = faker.name().firstName() + randomNumber;
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();
        invalidUsername = faker.regexify("[\\$#%]{5}");
    }

    @Test
    @DisplayName("Успешный update всех данных с помощью метода PATCH /api/v1/users/me/")
    public void successfulPatchUpdateUserTest() {

        String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

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

        String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

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

        String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

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

        String ipAddrRegexp = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

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
                    assertThat(patchUpdateResponse.username().get(0)).isEqualTo("Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.");
                });
    }


}

