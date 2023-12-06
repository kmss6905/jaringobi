package jaringobi.acceptance.expense.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.path.json.JsonPath;
import jaringobi.acceptance.APITest;
import jaringobi.acceptance.budget.BudgetAPI;
import jaringobi.acceptance.expense.ExpenseAPI;
import jaringobi.controller.query.expense.response.TodayExpensePerCategory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("오늘 지출 안내 API")
public class ExpenseTodayAPITest extends APITest {

    @Test
    @DisplayName("예산 설정 없이 지출 만 있는 경우")
    void ex1() {
        // Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String body = String.format(
                """
                {
                    "memo": "친구들이랑 점심 짬뽕",
                    "expenseMount": 10000,
                    "categoryId": 1,
                    "expenseDateTime": "%s"
                }
                """, formattedDateTime);

        ExpenseAPI.지출추가요청(body, accessToken);

        // When
        var response = ExpenseAPI.오늘지출안내조회(accessToken);
        JsonPath jsonPath = response.jsonPath();

        List<TodayExpensePerCategory> todayExpensePerCategories = jsonPath.getList("data.expensesPerCategory",
                TodayExpensePerCategory.class);

        // Then
        assertThat(response.response().statusCode()).isEqualTo(200);
        assertThat(jsonPath.getString("data.totalExpenseAmount")).isEqualTo("10000");
        assertThat(jsonPath.getBoolean("data.hasBudget")).isEqualTo(false);
        assertThat(jsonPath.getInt("data.budgetAmount")).isEqualTo(0);

        assertAll(
                () -> assertThat(todayExpensePerCategories).hasSize(1),
                () -> assertThat(todayExpensePerCategories).extracting("categoryId").containsExactly(1L),
                () -> assertThat(todayExpensePerCategories).extracting("paidAmount").containsExactly(10000),
                () -> assertThat(todayExpensePerCategories).extracting("hasCategoryBudget").containsExactly(false)
        );
    }

    @Test
    @DisplayName("예산 설정, 예산 카테고리는 있지만 지출이 하나도 없는 경우")
    void ex2() {
        // When
        var response = ExpenseAPI.오늘지출안내조회(accessToken);
        JsonPath jsonPath = response.jsonPath();

        List<TodayExpensePerCategory> todayExpensePerCategories = jsonPath.getList("data.expensesPerCategory",
                TodayExpensePerCategory.class);

        // Then
        assertThat(response.response().statusCode()).isEqualTo(200);
        assertThat(jsonPath.getInt("data.totalExpenseAmount")).isEqualTo(0);
        assertThat(jsonPath.getBoolean("data.hasBudget")).isEqualTo(false);
        assertThat(jsonPath.getInt("data.budgetAmount")).isEqualTo(0);

        assertAll(
                () -> assertThat(todayExpensePerCategories).hasSize(0)
        );
    }

    @Test
    @DisplayName("오늘 지출은 있지만, 예산 카테고리에 겹치치 않는 경우")
    void ex3() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String month = LocalDate.now().format(dateTimeFormatter);

        // Given
        // 한 달 식비(카테고리 3) 30만원 예산 설정
        String body = String.format(
                """
                    {
                        "budgetByCategories" : [
                            {
                                "categoryId": 3,
                                "money": 300000
                            }
                        ],
                        "month": "%s"
                    }
                    """, month
        );

        // When
        BudgetAPI.예산설정(body, accessToken);


        // Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String body2 = String.format(
                """
                {
                    "memo": "병원 감기 약값",
                    "expenseMount": 10000,
                    "categoryId": 4,
                    "expenseDateTime": "%s"
                }
                """, formattedDateTime);

        ExpenseAPI.지출추가요청(body2, accessToken);

        var response = ExpenseAPI.오늘지출안내조회(accessToken);
        JsonPath jsonPath = response.jsonPath();

        List<TodayExpensePerCategory> todayExpensePerCategories = jsonPath.getList("data.expensesPerCategory",
                TodayExpensePerCategory.class);

        assertThat(response.response().statusCode()).isEqualTo(200);
        assertThat(jsonPath.getString("data.totalExpenseAmount")).isEqualTo("10000");
        assertThat(jsonPath.getBoolean("data.hasBudget")).isEqualTo(true);
        assertThat(jsonPath.getInt("data.budgetAmount")).isEqualTo(300000);

        assertAll(
                () -> assertThat(todayExpensePerCategories).hasSize(1),
                () -> assertThat(todayExpensePerCategories).extracting("categoryId").containsExactly(4L),
                () -> assertThat(todayExpensePerCategories).extracting("paidAmount").containsExactly(10000),
                () -> assertThat(todayExpensePerCategories).extracting("hasCategoryBudget").containsExactly(false)
        );
    }

    @Test
    @DisplayName("예산 설정, 예산 카테고리는 있으며, 지출도 겺치는 경우")
    void ex4() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String month = LocalDate.now().format(dateTimeFormatter);

        // Given
        // 한 달 식비(카테고리 3) 30만원 예산 설정
        String body = String.format(
                """
                    {
                        "budgetByCategories" : [
                            {
                                "categoryId": 3,
                                "money": 300000
                            }
                        ],
                        "month": "%s"
                    }
                    """, month
        );

        // When
        BudgetAPI.예산설정(body, accessToken);

        // Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        String body2 = String.format(
                """
                {
                    "memo": "중식 점심 짬뽕",
                    "expenseMount": 10000,
                    "categoryId": 3,
                    "expenseDateTime": "%s"
                }
                """, formattedDateTime);

        ExpenseAPI.지출추가요청(body2, accessToken);


        var response = ExpenseAPI.오늘지출안내조회(accessToken);
        JsonPath jsonPath = response.jsonPath();

        assertThat(response.response().statusCode()).isEqualTo(200);
        assertThat(jsonPath.getString("data.totalExpenseAmount")).isEqualTo("10000");
        assertThat(jsonPath.getBoolean("data.hasBudget")).isEqualTo(true);
        assertThat(jsonPath.getInt("data.budgetAmount")).isEqualTo(300000);
        assertThat(jsonPath.getInt("data.dangerPercent")).isEqualTo(100);

        List<TodayExpensePerCategory> todayExpensePerCategories = jsonPath.getList("data.expensesPerCategory",
                TodayExpensePerCategory.class);

        assertAll(
                () -> assertThat(todayExpensePerCategories).hasSize(1),
                () -> assertThat(todayExpensePerCategories).extracting("categoryId").containsExactly(3L),
                () -> assertThat(todayExpensePerCategories).extracting("paidAmount").containsExactly(10000),
                () -> assertThat(todayExpensePerCategories).extracting("availableAmount").containsExactly(300000),
                () -> assertThat(todayExpensePerCategories).extracting("dangerPercent").containsExactly(100),
                () -> assertThat(todayExpensePerCategories).extracting("hasCategoryBudget").containsExactly(true)
        );
    }

    @Test
    @DisplayName("예산 설정, 예산 카테고리는 있으며, 일부는 겹치고 일부는 겹치지 않는 경우")
    void ex5() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String month = LocalDate.now().format(dateTimeFormatter);

        // Given
        // 한 달 식비(카테고리 3) 30만원 예산 설정
        String body = String.format(
                """
                    {
                        "budgetByCategories" : [
                            {
                                "categoryId": 3,
                                "money": 300000
                            }
                        ],
                        "month": "%s"
                    }
                    """, month
        );

        // When
        BudgetAPI.예산설정(body, accessToken);

        // Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = LocalDateTime.now().format(formatter);

        ExpenseAPI.지출추가요청(String.format(
                """
                {
                    "memo": "중식 점심 짬뽕",
                    "expenseMount": 10000,
                    "categoryId": 3,
                    "expenseDateTime": "%s"
                }
                """, formattedDateTime), accessToken);

        ExpenseAPI.지출추가요청(String.format(
                """
                {
                    "memo": "병원비 약 값",
                    "expenseMount": 50000,
                    "categoryId": 4,
                    "expenseDateTime": "%s"
                }
                """, formattedDateTime), accessToken);



        var response = ExpenseAPI.오늘지출안내조회(accessToken);
        JsonPath jsonPath = response.jsonPath();

        assertThat(response.response().statusCode()).isEqualTo(200);
        assertThat(jsonPath.getInt("data.totalExpenseAmount")).isEqualTo(60000);
        assertThat(jsonPath.getBoolean("data.hasBudget")).isEqualTo(true);
        assertThat(jsonPath.getInt("data.budgetAmount")).isEqualTo(300000);
        assertThat(jsonPath.getInt("data.dangerPercent")).isEqualTo((60000 / (300000 / 30)) * 100);

        List<TodayExpensePerCategory> todayExpensePerCategories = jsonPath.getList("data.expensesPerCategory",
                TodayExpensePerCategory.class);

        assertAll(
                () -> assertThat(todayExpensePerCategories).hasSize(2),
                () -> assertThat(todayExpensePerCategories).extracting("categoryId").containsExactly(3L, 4L),
                () -> assertThat(todayExpensePerCategories).extracting("paidAmount").containsExactly(10000, 50000),
                () -> assertThat(todayExpensePerCategories).extracting("availableAmount").containsExactly(300000, null),
                () -> assertThat(todayExpensePerCategories).extracting("dangerPercent").containsExactly(100, null),
                () -> assertThat(todayExpensePerCategories).extracting("hasCategoryBudget").containsExactly(true, false)
        );
    }
}
