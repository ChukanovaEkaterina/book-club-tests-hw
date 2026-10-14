package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class LoginSpec {

    public static ResponseSpecification successfulLoginResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/login/login_body_response_shema.json"))
            .log(ALL)
            .build();

    public static ResponseSpecification wrongPasswordAndUsernameLoginResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath("schemas/login/login_error_wrong_password_username_schema.json"))
            .log(ALL)
            .build();

    public static ResponseSpecification emptyFieldLoginResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas/login/login_error_empty_field_schema.json"))
            .log(ALL)
            .build();

}
