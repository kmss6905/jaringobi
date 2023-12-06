package jaringobi.acceptance.expense;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class ExpenseAPI {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    public static ExtractableResponse<Response> 지출추가요청(String body, String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, BEARER + token)
                .body(body)
                .post("/api/v1/expenditures")
                .andReturn()
                .then()
                .log().all().extract();
    }

    public static ExtractableResponse<Response> 지출수정요청(Long id, String body, String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, BEARER + token)
                .body(body)
                .put("/api/v1/expenditures/{id}", id)
                .andReturn()
                .then()
                .log().all().extract();
    }

    public static ExtractableResponse<Response> 지출삭제요청(Long id, String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, BEARER + token)
                .delete("/api/v1/expenditures/{id}", id)
                .andReturn()
                .then()
                .log().all().extract();
    }

    public static ExtractableResponse<Response> 오늘지출안내조회(String token) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, BEARER + token)
                .get("/api/v1/expenditures/today")
                .andReturn()
                .then()
                .log().all().extract();
    }
}
