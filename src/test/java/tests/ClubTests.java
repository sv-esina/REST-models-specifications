package tests;

import models.clubs.*;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static testdata.TestData.*;

public class ClubTests extends TestBase{
    String username;
    String password;
    String bookTitle;
    String newBookTitle;
    String bookAuthor;
    String newBookAuthor;
    Integer publicationYear;
    Integer newPublicationYear;
    String description;
    String newDescription;


    @BeforeEach
    public void prepareTestData() {
        username = randomUsername();
        password = randomPassword();
        bookTitle = randomBookTitle();
        bookAuthor = randomBookAuthors();
        publicationYear = FAKER.number().numberBetween(1700, 2025);
        description = FAKER.book().title() + " - " + FAKER.book().author();
        newBookTitle = randomBookTitle();
        newBookAuthor = randomBookAuthors();
        newPublicationYear = FAKER.number().numberBetween(1700, 2025);
        newDescription = FAKER.book().title() + " - " + FAKER.book().author();

    }

    @Test
    @DisplayName("Успешное создание клуба")
    public void successfulCreateClubTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        CreateClubBodyModel clubData = new CreateClubBodyModel(bookTitle, bookAuthor, publicationYear,
                description, TELEGRAM_LINK);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulCreateClubResponseModel clubResponse = api.clubs.createClub(clubData, accessToken);

        step("Проверка значений созданного клуба в полученном ответе", () -> {
            assertThat(clubResponse.id()).isGreaterThan(0);
            assertThat(clubResponse.bookTitle()).isEqualTo(bookTitle);
            assertThat(clubResponse.bookAuthors()).isEqualTo(bookAuthor);
            assertThat(clubResponse.publicationYear()).isEqualTo(publicationYear);
            assertThat(clubResponse.description()).isEqualTo(description);
            assertThat(clubResponse.telegramChatLink()).isEqualTo(TELEGRAM_LINK);
            assertThat(clubResponse.owner()).isEqualTo(registrationResponse.id());
            assertThat(clubResponse.members().get(0)).isEqualTo(registrationResponse.id());
            assertThat(clubResponse.reviews().isEmpty());
            assertThat(clubResponse.created()).isNotNull();
            assertThat(clubResponse.modified()).isNull();
        });
    }

    @Test
    @DisplayName("Выполнение запроса на создание клуба с пустым body")
    public void emptyBodyCreateClubTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        EmptyBodyCreateClubRequestModel emptyClubData = new EmptyBodyCreateClubRequestModel();

        String accessToken = "Bearer " + loginResponse.access();

        EmptyBodyCreateClubResponseModel clubResponse = api.clubs.emptyBodyCreateClub(emptyClubData, accessToken);

        step("Проверка значений созданного клуба в полученном ответе", () -> {
            assertThat(clubResponse.bookTitle().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(clubResponse.bookAuthors().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(clubResponse.publicationYear().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(clubResponse.description().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
            assertThat(clubResponse.telegramChatLink().get(0)).isEqualTo(ERROR_REQUIRED_FIELD);
        });
    }

    @Test
    @DisplayName("Успешный поиск клуба по id")
    public void getClubByIdTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        CreateClubBodyModel clubData = new CreateClubBodyModel(bookTitle, bookAuthor, publicationYear,
                description, TELEGRAM_LINK);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulCreateClubResponseModel clubResponse = api.clubs.createClub(clubData, accessToken);

        SuccessfulGetClubResponseModel getClubResponse = api.clubs.getClub(clubResponse.id(), accessToken);

        step("Проверка соответствия значений найденного клуба в полученном ответе", () -> {
            assertThat(getClubResponse.id()).isEqualTo(clubResponse.id());
            assertThat(getClubResponse.bookTitle()).isEqualTo(clubResponse.bookTitle());
            assertThat(getClubResponse.bookAuthors()).isEqualTo(clubResponse.bookAuthors());
            assertThat(getClubResponse.publicationYear()).isEqualTo(clubResponse.publicationYear());
            assertThat(getClubResponse.description()).isEqualTo(clubResponse.description());
            assertThat(getClubResponse.telegramChatLink()).isEqualTo(clubResponse.telegramChatLink());
            assertThat(getClubResponse.owner()).isEqualTo(clubResponse.owner());
            assertThat(getClubResponse.members().get(0)).isEqualTo(clubResponse.members().get(0));
            assertThat(getClubResponse.reviews().isEmpty());
            assertThat(getClubResponse.created()).isEqualTo(clubResponse.created());
            assertThat(getClubResponse.modified()).isNull();
        });
    }

    @Test
    @DisplayName("Поиск несуществующего клуба по id")
    public void getClubByNonExistentIdTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        CreateClubBodyModel clubData = new CreateClubBodyModel(bookTitle, bookAuthor, publicationYear,
                description, TELEGRAM_LINK);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulCreateClubResponseModel clubResponse = api.clubs.createClub(clubData, accessToken);

        NonExistentGetClubResponseModel getClubResponse = api.clubs.getNonExistentClub(clubResponse.id()+1, accessToken);

        step("Проверка полученной ошибки", () -> {
            assertThat(getClubResponse.detail()).isEqualTo(ERROR_CLUB_MATCHES);
        });
    }

    @Test
    @DisplayName("Успешный Update клуба по id")
    public void updateClubByIdTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        CreateClubBodyModel clubData = new CreateClubBodyModel(newBookTitle, newBookAuthor, newPublicationYear,
                newDescription, TELEGRAM_LINK);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulCreateClubResponseModel clubResponse = api.clubs.createClub(clubData, accessToken);

        SuccessfulUpdateClubResponseModel updateClubResponse = api.clubs.updateClub(clubResponse.id(), clubData, accessToken);

        step("Проверка отредактированных значений клуба в полученном ответе", () -> {
            assertThat(updateClubResponse.id()).isEqualTo(clubResponse.id());
            assertThat(updateClubResponse.bookTitle()).isEqualTo(newBookTitle);
            assertThat(updateClubResponse.bookAuthors()).isEqualTo(newBookAuthor);
            assertThat(updateClubResponse.publicationYear()).isEqualTo(newPublicationYear);
            assertThat(updateClubResponse.description()).isEqualTo(newDescription);
            assertThat(updateClubResponse.telegramChatLink()).isEqualTo(clubResponse.telegramChatLink());
            assertThat(updateClubResponse.owner()).isEqualTo(clubResponse.owner());
            assertThat(updateClubResponse.members().get(0)).isEqualTo(clubResponse.members().get(0));
            assertThat(updateClubResponse.reviews().isEmpty());
            assertThat(updateClubResponse.created()).isEqualTo(clubResponse.created());
            assertThat(updateClubResponse.modified()).isNotNull();
        });
    }

    @Test
    @DisplayName("Успешное удаление клуба по id")
    public void deleteClubByIdTest() {

        RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel registrationResponse = api.users.register(registrationData);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = api.auth.login(loginData);

        CreateClubBodyModel clubData = new CreateClubBodyModel(newBookTitle, newBookAuthor, newPublicationYear,
                newDescription, TELEGRAM_LINK);

        String accessToken = "Bearer " + loginResponse.access();

        SuccessfulCreateClubResponseModel clubResponse = api.clubs.createClub(clubData, accessToken);

        api.clubs.deleteClub(clubResponse.id(), accessToken);

        NonExistentClubResponseModel nonExistentClubResponse = api.clubs.noSuchClub(clubResponse.id(), accessToken);

        step("Проверка фактического удаления клуба", () -> {
            assertThat(nonExistentClubResponse.detail()).isEqualTo(ERROR_CLUB_MATCHES);
        });
    }
}
