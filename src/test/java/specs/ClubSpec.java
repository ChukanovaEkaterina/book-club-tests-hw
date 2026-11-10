package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ClubSpec {

    public static ResponseSpecification successfulClubListResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_list_response_schema.json"))
            .log(ALL)
            .build();

    public static ResponseSpecification invalidPageClubListResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(404)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_list_error_response_schema.json"))
            .log(ALL)
            .build();

    public static ResponseSpecification successfulCreateClubResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_create_response_schema.json"))
            .log(ALL)
            .build();

    public static ResponseSpecification successfulUpdateClubResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/club/club_update_response_schema.json"))
            .log(ALL)
            .build();
}
