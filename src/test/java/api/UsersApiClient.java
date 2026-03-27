package api;

import io.qameta.allure.Step;
import models.registration.*;
import models.update.*;

import static io.restassured.RestAssured.given;
import static specs.BaseSpec.baseRequestSpec;
import static specs.registration.RegistrationSpec.*;
import static specs.update.UpdateUserSpec.*;

public class UsersApiClient {

    @Step("Отправка запроса на регистрацию пользователя")
    public SuccessfulRegistrationResponseModel register (RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);
    }

    @Step("Повторная регистрация пользователя")
    public ExistingUserResponseModel registerExistingUser (RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongCredentialsRegisterResponseSpec)
                .extract().as(ExistingUserResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию с пустыми параметрами")
    public EmptyParamsRegistrationResponseModel setEmptyParamsRegister (RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongParamsRegistrationResponseSpec)
                .extract()
                .as(EmptyParamsRegistrationResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию с пустыми параметрами")
    public EmptyBodyRegistrationResponseModel setEmptyBodyRegister (WrongRegistrationBodyModel wrongRegistrationData) {
        return given(baseRequestSpec)
                .body(wrongRegistrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongParamsRegistrationResponseSpec)
                .extract()
                .as(EmptyBodyRegistrationResponseModel.class);
    }

    @Step("Отправка запроса на регистрацию с пустыми параметрами")
    public InvalidUsernameUserResponseModel setInvalidUsernameRegister (RegistrationBodyModel invalidRegistrationData) {
        return given(baseRequestSpec)
                .body(invalidRegistrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(invalidUsernameRegistrationResponseSpec)
                .extract()
                .as(InvalidUsernameUserResponseModel.class);
    }

    @Step("Отправка null в параметрах для регистрации")
    public EmptyParamsRegistrationResponseModel setNullParamsRegister (RegistrationBodyModel registrationData) {
        return given(baseRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(wrongParamsRegistrationResponseSpec)
                .extract()
                .as(EmptyParamsRegistrationResponseModel.class);
    }


    @Step("Отправка запроса PUT на update данных пользователя")
    public SuccessfulUpdateUserResponseModel updatePut (UpdateBodyModel updateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(successfulUpdateResponseSpec)
                .extract()
                .as(SuccessfulUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса PUT с одним параметром username")
    public WrongParamsUpdateUserResponseModel setOnlyUsernameUpdatePut (WrongParamsUpdateBodyModel wrongParamsUpdateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(wrongParamsUpdateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(wrongParamsUpdateUserSpec)
                .extract()
                .as(WrongParamsUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса PUT с пустым параметром username")
    public EmptyUsernameUpdateUserResponseModel setEmptyUsernameUpdatePut (UpdateBodyModel updateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(updateData)
                .when()
                .put("/users/me/")
                .then()
                .spec(emptyUsernameUpdateUserSpec)
                .extract()
                .as(EmptyUsernameUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса PATCH на update всех данных пользователя")
    public SuccessfulPatchUpdateUserResponseModel updatePatch (PatchUpdateBodyModel patchUpdateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(patchUpdateData)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса PATCH на update только параметра username")
    public SuccessfulPatchUpdateUserResponseModel updateOnlyUsernamePatch (PatchUsernameParamUpdateBodyModel patchUsernameUpdateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(patchUsernameUpdateData)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneParamPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса PATCH на update только параметра firstName")
    public SuccessfulPatchUpdateUserResponseModel updateOnlyFirstNamePatch (PatchFirstNameParamUpdateBodyModel patchUpdateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(patchUpdateData)
                .when()
                .patch("/users/me/")
                .then()
                .spec(successfulOneParamPatchUserUpdateSpec)
                .extract()
                .as(SuccessfulPatchUpdateUserResponseModel.class);
    }

    @Step("Отправка запроса PATCH с невалидным username")
    public WrongParamPatchUpdateResponseModel setInvalidUsernameUpdatePatch (PatchUpdateBodyModel patchUpdateData, String accessToken) {
        return given(baseRequestSpec)
                .header("Authorization", accessToken)
                .body(patchUpdateData)
                .when()
                .patch("/users/me/")
                .then()
                .spec(wrongPatchUsernameUpdateUserSpec)
                .extract()
                .as(WrongParamPatchUpdateResponseModel.class);
    }
}
