package jaringobi.dto.request;

import jaringobi.controller.search.CategoryIds;
import jaringobi.domain.budget.Money;
import jaringobi.dto.SearchOrder;
import jaringobi.dto.SearchSort;
import jaringobi.exception.expense.ExpenseSearchDateNotNullException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
public class ExpenseSearchCondition {

    protected final static String DATE_FORMAT_PATTERN = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";
    protected final static  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected final static int DEFAULT_PAGE_SIZE = 10;
    protected final static int MIN_PAGE_SIZE = 10;
    protected final static int MAX_PAGE_SIZE = 100;
    protected final static int DEFAULT_PAGE = 0;

    private final LocalDateTime start;
    private final LocalDateTime end;
    private Money min;
    private Money max;
    private CategoryIds categoryIds;
    private int page = DEFAULT_PAGE;
    private int size = DEFAULT_PAGE_SIZE;
    private SearchOrder order = SearchOrder.DESC;
    private SearchSort sort = SearchSort.EXPENSE_DATE;

    @Builder
    public ExpenseSearchCondition(String start, String end, Integer min, Integer max, List<Long> categoryIds,
            Integer page, Integer size, SearchOrder order, SearchSort sort) {
        verifyNotNullDate(start, end);
        this.start = toLocalDateTime(start);
        this.end = toLocalDateTime(end);

        if (Objects.nonNull(min)) {
            this.min = new Money(min);
        }

        if (Objects.nonNull(max)) {
            this.max = new Money(max);
        }
        this.categoryIds = CategoryIds.of(categoryIds);
        if (Objects.nonNull(page)) {
            setPage(page);
        }

        if (Objects.nonNull(size)) {
            setSize(size);
        }

        if (Objects.nonNull(order)) {
            setOrder(order);
        }

        if (Objects.nonNull(sort)) {
            setSort(sort);
        }
    }

    private void verifyNotNullDate(String start, String end) {
        if (Objects.isNull(start) || Objects.isNull(end))  {
            throw new ExpenseSearchDateNotNullException();
        }
    }

    private void setOrder(SearchOrder order) {
        this.order = Objects.requireNonNull(order);
    }

    private void setSort(SearchSort sort) {
        this.sort = Objects.requireNonNull(sort);
    }

    private void setSize(Integer size) {
        if (size > DEFAULT_PAGE_SIZE) {
            return;
        }
        this.size = size;
    }

    private void setPage(Integer page) {
        if (page < DEFAULT_PAGE) {
           return;
        }
        this.page = page;
    }

    private LocalDateTime toLocalDateTime(String date) {
        if (!isMatch(date)) {
            throw new IllegalArgumentException();
        }
        return parseDate(date);
    }

    private boolean isMatch(String date) {
        return Pattern.matches(DATE_FORMAT_PATTERN, date);
    }

    private LocalDateTime parseDate(String date) {
        LocalDate ld = LocalDate.parse(date, formatter);
        return LocalDateTime.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth(), 0, 0, 0);
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}
