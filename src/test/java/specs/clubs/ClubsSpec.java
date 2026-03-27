package specs.clubs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class ClubsSpec {

    public static ResponseSpecification successfulCreateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/successful_create_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("members", notNullValue())
            .expectBody("reviews", empty())
            .expectBody("created", notNullValue())
            .expectBody("modified", nullValue())
            .build();

    public static ResponseSpecification emptyBodyCreateClubResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/empty_body_create_club_response_schema.json"))
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .build();

    public static ResponseSpecification successfulGetClubSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/successful_get_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("members", notNullValue())
            .expectBody("reviews", empty())
            .expectBody("created", notNullValue())
            .expectBody("modified", nullValue())
            .build();

    public static ResponseSpecification successfulUpdateClubSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/successful_update_club_response_schema.json"))
            .expectBody("id", notNullValue())
            .expectBody("bookTitle", notNullValue())
            .expectBody("bookAuthors", notNullValue())
            .expectBody("publicationYear", notNullValue())
            .expectBody("description", notNullValue())
            .expectBody("telegramChatLink", notNullValue())
            .expectBody("owner", notNullValue())
            .expectBody("members", notNullValue())
            .expectBody("reviews", empty())
            .expectBody("created", notNullValue())
            .expectBody("modified", notNullValue())
            .build();

    public static ResponseSpecification DeleteClubSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(204)
            .build();

    public static ResponseSpecification nonExistentClubSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/clubs/successful_delete_club_response_schema.json"))
            .build();
}
