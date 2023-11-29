package jaringobi.acceptance.budget;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import jaringobi.acceptance.APITest;
import jaringobi.dto.response.BudgetByCategoryResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("예산 API 테스트")
public class BudgetAPITest extends APITest {

    @Nested
    @DisplayName("[예산 설정] /api/v1/budget ")
    class BudgetCreate {

        @Test
        @DisplayName("성공 200")
        void successCreateBudget() {
            // Given
            String body = """
                    {
                        "budgetByCategories" : [
                            {
                                "categoryId": 1,
                                "money": 1
                            },
                            {
                                "categoryId": 2,
                                "money": 9000
                            }
                        ],
                        "month": "2023-10"
                    }
                    """;
            // When
            var response = BudgetAPI.예산설정(body, accessToken);

            // Then
            assertThat(response.response().statusCode()).isEqualTo(201);
            assertThat(response.header("Location")).isEqualTo("/api/v1/budget/1");
        }

        @Test
        @DisplayName("실패 400 - 중복된 카테고리 값")
        void failDuplicatedCategory() {
            String body = """
                    {
                        "budgetByCategories" : [
                            {
                                "categoryId": 1,
                                "money": 1
                            },
                            {
                                "categoryId": 1,
                                "money": 9000
                            }
                        ],
                        "month": "2023-10"
                    }
                    """;
            // When
            var response = BudgetAPI.예산설정(body, accessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(400);
            assertThat(jsonPath.getString("code")).isEqualTo("E001");
            assertThat(jsonPath.getString("message")).isEqualTo("필드 값 에러");
            assertThat(jsonPath.getString("errorFields[0].message")).isEqualTo("중복 없이 적어도 하나의 카테고리별 예산이 포함되어야 합니다.");
        }

        @Test
        @DisplayName("실패 400 - 비어있는 카테고리 예산")
        void failCategoryIsNull() {
            // Given
            String body = """
                    {
                        "month": "2023-10"
                    }
                    """;
            // When
            var response = BudgetAPI.예산설정(body, accessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(400);
            assertThat(jsonPath.getString("code")).isEqualTo("E001");
            assertThat(jsonPath.getString("message")).isEqualTo("필드 값 에러");
            assertThat(jsonPath.getString("errorFields[0].message")).isEqualTo("중복 없이 적어도 하나의 카테고리별 예산이 포함되어야 합니다.");
        }

        @Test
        @DisplayName("실패 400 - 잘못된 날짜 포맷형식")
        void failInvalidDateFormat() {
            // Given
            String body = """
                    {
                        "budgetByCategories" : [
                            {
                                "categoryId": 1,
                                "money": 1
                            },
                            {
                                "categoryId": 2,
                                "money": 9000
                            }
                        ],
                        "month": "2023"
                    }
                    """;
            // When
            var response = BudgetAPI.예산설정(body, accessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(400);
            assertThat(jsonPath.getString("code")).isEqualTo("E001");
            assertThat(jsonPath.getString("message")).isEqualTo("필드 값 에러");
            assertThat(jsonPath.getString("errorFields[0].message")).isEqualTo("예산 날짜는 'yyyy-MM' 형식이어야 합니다.");
        }


    }

    @Nested
    @DisplayName("[예산 삭제] /api/v1/budget/{id}")
    class BudgetDelete {

        @Test
        @DisplayName("성공 204")
        void successDeleteBudget() {
            // Given
            saveBudget();

            // When
            var response = BudgetAPI.예산삭제요청(1, accessToken);

            // Then
            assertThat(response.response().statusCode()).isEqualTo(204);
        }

        @Test
        @DisplayName("실패 404 - 존재하지 않는 예산 정보")
        void failNotExistedBudget() {
            // When
            var response = BudgetAPI.예산삭제요청(1, accessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(404);
            assertThat(jsonPath.getString("code")).isEqualTo("B003");
            assertThat(jsonPath.getString("message")).isEqualTo("존재하지 않는 예산입니다.");
        }

        @Test
        @DisplayName("실패 403 - 권한 없는 유저 요청")
        void failNoPermission() {
            saveBudget();

            // When
            var response = BudgetAPI.예산삭제요청(1, anotherUserAccessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(403);
            assertThat(jsonPath.getString("code")).isEqualTo("AUTH_03");
            assertThat(jsonPath.getString("message")).isEqualTo("해당 작업을 수행할 권한이 없습니다.");
        }

    }

    @Nested
    @DisplayName("[예산 조회] /api/v1/budget/{id}")
    class BudgetFetch {

        @Test
        @DisplayName("성공 200")
        void successFetchBudget() {
            // Given
            saveBudget();

            // When
            var response = BudgetAPI.예산조회요청(1L, accessToken);
            var jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(200);
            assertThat(jsonPath.getString("code")).isEqualTo("200");
            assertThat(jsonPath.getString("data.month")).isEqualTo("2023-10-01");
            assertThat(jsonPath.getString("data.createdAt")).isNotEmpty();
            assertThat(jsonPath.getString("data.updatedAt")).isNotEmpty();

            List<BudgetByCategoryResponse> budgetByCategoryResponses = jsonPath.getList("data.budgetByCategories",
                    BudgetByCategoryResponse.class);
            assertAll(
                    () -> assertThat(budgetByCategoryResponses).extracting("id").isNotEmpty(),
                    () -> assertThat(budgetByCategoryResponses).extracting("createdAt").isNotEmpty(),
                    () -> assertThat(budgetByCategoryResponses).extracting("updatedAt").isNotEmpty(),
                    () -> assertThat(budgetByCategoryResponses).hasSize(2),
                    () -> assertThat(budgetByCategoryResponses).extracting("categoryId").containsExactly(1L, 2L),
                    () -> assertThat(budgetByCategoryResponses).extracting("money").containsExactly(1, 9000)
            );
        }

        @Test
        @DisplayName("실패 404 - 존재하지 않는 예산 정보 조회")
        void failNotExistedBudget() {
            // When
            var response = BudgetAPI.예산조회요청(1L, accessToken);
            var jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(404);
            assertThat(jsonPath.getString("code")).isEqualTo("B003");
            assertThat(jsonPath.getString("message")).isEqualTo("존재하지 않는 예산입니다.");
        }

        @Test
        @DisplayName("실패 403 - 권한 없는 유저 요청")
        void failNoPermission() {
            saveBudget();

            // When
            var response = BudgetAPI.예산조회요청(1, anotherUserAccessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(403);
            assertThat(jsonPath.getString("code")).isEqualTo("AUTH_03");
            assertThat(jsonPath.getString("message")).isEqualTo("해당 작업을 수행할 권한이 없습니다.");
        }
    }

    @Nested
    @DisplayName("[예산 카테고리 추가] /api/v1/budget/{id}/category")
    class BudgetCategoryAdd {

        @Test
        @DisplayName("성공 200")
        void successFetchBudget() {
            // Given
            saveBudget();

            String body = """
                            {
                                "categoryId": 10,
                                "money": 10000
                            }
                    """;

            // When
            var response = BudgetAPI.예산카테고리추가요청(body, 1L, accessToken);

            // Then
            assertThat(response.response().statusCode()).isEqualTo(201);

            // Then
            var response2 = BudgetAPI.예산조회요청(1L, accessToken);
            var jsonPath = response2.jsonPath();

            assertThat(jsonPath.getString("code")).isEqualTo("200");
            assertThat(jsonPath.getString("data.month")).isEqualTo("2023-10-01");
            assertThat(jsonPath.getString("data.createdAt")).isNotEmpty();
            assertThat(jsonPath.getString("data.updatedAt")).isNotEmpty();

            List<BudgetByCategoryResponse> budgetByCategoryResponses = jsonPath.getList("data.budgetByCategories",
                    BudgetByCategoryResponse.class);
            assertAll(
                    () -> assertThat(budgetByCategoryResponses).extracting("id").isNotEmpty(),
                    () -> assertThat(budgetByCategoryResponses).extracting("createdAt").isNotEmpty(),
                    () -> assertThat(budgetByCategoryResponses).extracting("updatedAt").isNotEmpty(),
                    () -> assertThat(budgetByCategoryResponses).hasSize(3),
                    () -> assertThat(budgetByCategoryResponses).extracting("categoryId").containsExactly(1L, 2L, 10L),
                    () -> assertThat(budgetByCategoryResponses).extracting("money").containsExactly(1, 9000, 10000)
            );

        }

        @Test
        @DisplayName("실패 400 - 중복된 카테고리 추가 시 예외를 던진다.")
        void failDuplicatedCategory() {
            // Given
            saveBudget();

            String body = """
                            {
                                "categoryId": 1,
                                "money": 1000
                            }
                    """;

            // When
            var response = BudgetAPI.예산카테고리추가요청(body, 1L, accessToken);
            JsonPath jsonPath = response.jsonPath();

            // Then
            assertThat(response.response().statusCode()).isEqualTo(409);
            assertThat(jsonPath.getString("code")).isEqualTo("B004");
            assertThat(jsonPath.getString("message")).isEqualTo("해당 카테고리로 예산 항목이 이미 존재합니다. 중복 카테고리 등록 불가");
        }
    }

    private void saveBudget() {
        String body = """
                {
                    "budgetByCategories" : [
                        {
                            "categoryId": 1,
                            "money": 1
                        },
                        {
                            "categoryId": 2,
                            "money": 9000
                        }
                    ],
                    "month": "2023-10"
                }
                """;
        BudgetAPI.예산설정(body, accessToken);
    }
}
