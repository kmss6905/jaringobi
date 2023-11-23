package jaringobi.dto.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jaringobi.dto.SearchOrder;
import jaringobi.dto.SearchSort;
import jaringobi.exception.expense.ExpenseSearchDateNotNullException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExpenseSearchConditionTest {


    @Test
    @DisplayName("지출 검색 조건 객체 만들기 성공")
    void successCreateExpenseSearchCondition() {
        assertThatCode(() -> ExpenseSearchCondition.builder()
                .sort(SearchSort.EXPENSE_DATE)
                .start("2023-10-20")
                .end("2023-11-20")
                .min(1000)
                .max(10000)
                .order(SearchOrder.ASC)
                .page(1)
                .size(10)
                .categoryIds(List.of(1L, 2L))
                .build())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("start, end 가 없으면 객체 만들기 실패")
    void throwExceptionWhenStartAndEndIsNull() {
        assertThatThrownBy(() -> ExpenseSearchCondition.builder()
                .sort(SearchSort.EXPENSE_DATE)
                .order(SearchOrder.ASC)
                .page(1)
                .size(10)
                .categoryIds(List.of(1L, 2L))
                .build())
                .isInstanceOf(ExpenseSearchDateNotNullException.class);
    }

    @Test
    @DisplayName("page 가 없으면 기본 page 로 객체 생성")
    void defaultPageWhenPageIsNull() {
        ExpenseSearchCondition searchCondition = ExpenseSearchCondition.builder()
                .sort(SearchSort.EXPENSE_DATE)
                .order(SearchOrder.ASC)
                .start("2023-10-20")
                .end("2023-11-20")
                .size(10)
                .categoryIds(List.of(1L, 2L))
                .build();

        int page = searchCondition.getPage();
        assertThat(page).isEqualTo(ExpenseSearchCondition.DEFAULT_PAGE);
    }


    @Test
    @DisplayName("page 는 0 보다 작을 수 없다.")
    void pageCantNegative() {
        ExpenseSearchCondition searchCondition = ExpenseSearchCondition.builder()
                .sort(SearchSort.EXPENSE_DATE)
                .order(SearchOrder.ASC)
                .start("2023-10-20")
                .end("2023-11-20")
                .size(10000)
                .page(-1)
                .categoryIds(List.of(1L, 2L))
                .build();

        int page = searchCondition.getPage();
        assertThat(page).isEqualTo(ExpenseSearchCondition.DEFAULT_PAGE);
    }

    @Test
    @DisplayName("size 가 없으면 기본 size 로 객체 생성")
    void defaultSizeWhenSizeIsNull() {
        ExpenseSearchCondition searchCondition = ExpenseSearchCondition.builder()
                .sort(SearchSort.EXPENSE_DATE)
                .order(SearchOrder.ASC)
                .start("2023-10-20")
                .end("2023-11-20")
                .page(1)
                .categoryIds(List.of(1L, 2L))
                .build();

        int size = searchCondition.getSize();
        assertThat(size).isEqualTo(ExpenseSearchCondition.DEFAULT_PAGE_SIZE);
    }

    @Test
    @DisplayName("size 는 최대 사이즈를 넘어갈 수 없다.")
    void sizeCantOverMaxPageSize() {
        ExpenseSearchCondition searchCondition = ExpenseSearchCondition.builder()
                .sort(SearchSort.EXPENSE_DATE)
                .order(SearchOrder.ASC)
                .start("2023-10-20")
                .end("2023-11-20")
                .size(10000)
                .page(1)
                .categoryIds(List.of(1L, 2L))
                .build();

        // When
        int size = searchCondition.getSize();

        // Then
        assertThat(size).isEqualTo(ExpenseSearchCondition.DEFAULT_PAGE_SIZE);
    }
}
