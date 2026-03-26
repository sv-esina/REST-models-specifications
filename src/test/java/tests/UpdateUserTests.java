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
import static org.assertj.core.api.Assertions.assertThat;
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

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        step("Проверка полученного ответа", () -> {
            assertThat(registrationResponse.id()).isGreaterThan(0);
            assertThat(registrationResponse.username()).isEqualTo(username);
            assertThat(registrationResponse.firstName()).isEqualTo("");
            assertThat(registrationResponse.lastName()).isEqualTo("");
            assertThat(registrationResponse.email()).isEqualTo("");
            assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);

        });

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        step("Проверка полученных токенов", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        UpdateBodyModel updateData = new UpdateBodyModel(username, firstName, lastName, email);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulUpdateUserResponseModel updateResponse = api.users.updatePut(updateData, accessToken);

        step("Проверка обновленных данных", () -> {
            assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(username);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);
            assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
            assertThat(registrationResponse.remoteAddr()).isEqualTo(updateResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Update данных с отсутствием обязательных параметров (PUT)")
    public void wrongParamsPutUpdateUserTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = "Bearer " + loginResponse.access();

        step("Проверка полученных токенов", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        WrongParamsUpdateBodyModel wrongParamsUpdateData = new WrongParamsUpdateBodyModel(username);

        WrongParamsUpdateUserResponseModel updateResponse = api.users.setOnlyUsernameUpdatePut(wrongParamsUpdateData, accessToken);

        step("Проверка текста полученных ошибок", () -> {
            assertThat(updateResponse.firstName().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(updateResponse.lastName().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(updateResponse.email().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
        });
    }

    @Test
    @DisplayName("Update данных с пустым username (PUT)")
    public void emptyUsernamePutUpdateUserTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        String accessToken = "Bearer " + loginResponse.access();

        step("Проверка полученных токенов", () -> {
            assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
        });

        UpdateBodyModel updateData = new UpdateBodyModel(emptyUsername, firstName, lastName, email);

        EmptyUsernameUpdateUserResponseModel updateResponse = api.users.setEmptyUsernameUpdatePut(updateData, accessToken);

        step("Проверка текста полученной ошибки", () -> {
            assertThat(updateResponse.username().get(0)).isEqualTo(ERROR_BLANK_FIELD);
        });
    }

}

