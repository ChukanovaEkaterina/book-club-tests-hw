package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ReviewsSpec {
    public static final ResponseSpecification successfulReviewResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/reviews/review_response_schema.json")).build();
    public static final ResponseSpecification successfulCreateReviewResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .expectBody(matchesJsonSchemaInClasspath("schemas/reviews/review_response_schema.json")).build();
    public static final ResponseSpecification successfulReviewsListResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath("schemas/reviews/review_list_response_schema.json")).build();

    public static final ResponseSpecification unauthorizedUserReviewResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(401)
            .expectBody("detail", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyOrNullString()))
            .build();

    public static final ResponseSpecification blankFieldsReviewResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(400)
            .expectBody(matchesJsonSchemaInClasspath("schemas/reviews/validation_error_response_schema.json"))
            .build();

    public static final ResponseSpecification notOwnerReviewResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(403)
            .expectBody("detail", org.hamcrest.Matchers.equalTo("You do not have permission to perform this action."))
            .build();

    public static final ResponseSpecification successfulDeleteReviewResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(204)
            .expectBody(org.hamcrest.Matchers.emptyString())
            .build();
}
