package jaringobi.domain.budget;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jaringobi.exception.budget.InvalidBudgetException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BudgetYearMonth {

    private static final String YEAR_MONTH_PATTERN = "\\d{4}-\\d{1,2}";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M");

    @Column(name = "budget_month")
    @Temporal(value = TemporalType.DATE)
    private LocalDate month;

    private BudgetYearMonth(LocalDate month) {
        this.month = month;
    }

    public static BudgetYearMonth fromString(String yearMonthString) {
        if (!isValidYearMonthFormat(yearMonthString)) {
            throw new InvalidBudgetException();
        }
        YearMonth yearMonth = YearMonth.parse(yearMonthString, formatter);
        return new BudgetYearMonth(LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1));
    }

    private static boolean isValidYearMonthFormat(String yearMonthString) {
        return yearMonthString.matches(YEAR_MONTH_PATTERN);
    }

    public boolean isEmptyMonth() {
        return Objects.isNull(this.month);
    }

    public LocalDate getMonth() {
        return month;
    }
}
