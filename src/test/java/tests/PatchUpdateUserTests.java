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

        PatchUpdateBodyModel patchUpdateData = new PatchUpdateBodyModel(newUsername, firstName, lastName, email);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulPatchUpdateUserResponseModel updateResponse = api.users.updatePatch(patchUpdateData, accessToken);

        step("Проверка обновленных данных", () -> {
            assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(newUsername);
            assertThat(updateResponse.firstName()).isEqualTo(firstName);
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);
            assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
            assertThat(registrationResponse.remoteAddr()).isEqualTo(updateResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Успешный update только параметра username с помощью метода PATCH /api/v1/users/me/")
    public void successfulPatchUpdateOnlyUsernameParamTest() {

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

        PatchUsernameParamUpdateBodyModel patchUsernameUpdateData = new PatchUsernameParamUpdateBodyModel(newUsername);

        SuccessfulPatchUpdateUserResponseModel updateResponse = api.users.updateOnlyUsernamePatch(patchUsernameUpdateData, accessToken);

        step("Проверка обновленных данных", () -> {
            assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(newUsername);
            assertThat(updateResponse.firstName()).isEqualTo("");
            assertThat(updateResponse.lastName()).isEqualTo("");
            assertThat(updateResponse.email()).isEqualTo("");
            assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
            assertThat(registrationResponse.remoteAddr()).isEqualTo(updateResponse.remoteAddr());
        });
    }


    @Test
    @DisplayName("Успешный update только параметра firstName с помощью метода PATCH /api/v1/users/me/")
    public void successfulPatchUpdateOnlyFirstNameParamTest() {

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

        PatchFirstNameParamUpdateBodyModel patchUpdateData = new PatchFirstNameParamUpdateBodyModel(firstName);

        String accessToken = "Bearer " + loginResponse.access();


        SuccessfulPatchUpdateUserResponseModel patchUpdateResponse = api.users.updateOnlyFirstNamePatch(patchUpdateData, accessToken);

        step("Проверка обновленных данных", () -> {
            assertThat(patchUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(patchUpdateResponse.username()).isEqualTo(username);
            assertThat(patchUpdateResponse.firstName()).isEqualTo(firstName);
            assertThat(patchUpdateResponse.lastName()).isEqualTo("");
            assertThat(patchUpdateResponse.email()).isEqualTo("");
            assertThat(registrationResponse.remoteAddr()).matches(IP_ADDRESS_REGEXP);
            assertThat(registrationResponse.remoteAddr()).isEqualTo(patchUpdateResponse.remoteAddr());
        });
    }

    @Test
    @DisplayName("Update пользователя с недопустимым символом в username (PATCH)")
    public void invalidUsernameParamPatchUpdateTest() {

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

        PatchUpdateBodyModel patchUpdateData = new PatchUpdateBodyModel(invalidUsername, firstName, lastName, email);

        String accessToken = "Bearer " + loginResponse.access();

        WrongParamPatchUpdateResponseModel patchUpdateResponse = api.users.setInvalidUsernameUpdatePatch(patchUpdateData, accessToken);

        step("Проверка текста полученной ошибки", () -> {
            assertThat(patchUpdateResponse.username().get(0)).isEqualTo(ERROR_INVALID_USERNAME);
        });
    }


}

