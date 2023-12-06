package jaringobi.domain.expense;


import static jaringobi.domain.budget.QBudget.budget;
import static jaringobi.domain.budget.QCategoryBudget.categoryBudget;
import static jaringobi.domain.category.QCategory.category;
import static jaringobi.domain.expense.QExpense.expense;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jaringobi.controller.query.expense.response.TodayExpensePerCategory;
import jaringobi.controller.search.CategoryExpenseSum;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.budget.Money;
import jaringobi.domain.user.AppUser;
import jaringobi.dto.SearchSort;
import jaringobi.dto.request.ExpenseSearchCondition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ExpenseQueryRepositoryImpl implements ExpenseQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Page<Expense> searchByCondition(AppUser appUser, ExpenseSearchCondition condition) {
        Pageable pageable = condition.toPageable();

        List<Expense> expenses = jpaQueryFactory.selectFrom(expense)
                .where(eqUser(appUser.userId()),
                        betweenAmounts(condition.getMin(), condition.getMax()),
                        inCategories(condition.getCategoryIds().getIds()),
                        betweenDate(condition.getStart(), condition.getEnd())
                )
                .limit((pageable.getPageSize()))
                .offset(pageable.getOffset())
                .orderBy(findCriteria(condition))
                .fetch();

        // total count
        JPAQuery<Long> totalCount = jpaQueryFactory.select(expense.id.count())
                .where(eqUser(appUser.userId()),
                        betweenAmounts(condition.getMin(), condition.getMax()),
                        inCategories(condition.getCategoryIds().getIds()),
                        betweenDate(condition.getStart(), condition.getEnd()))
                .from(expense);
        return PageableExecutionUtils.getPage(expenses, pageable, totalCount::fetchOne);
    }

    @Override
    public List<TodayExpensePerCategory> todayTotalExpense(AppUser appUser) {
        LocalDate localDate = LocalDate.now();
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

        return jpaQueryFactory.select(
                        Projections.fields(TodayExpensePerCategory.class,
                                category.id.as("categoryId"),
                                expense.money.amount.sum().as("paidAmount")))
                .from(expense)
                .join(category).on(category.eq(expense.category))
                .where(
                        expense.expenseAt.between(startOfDay, endOfDay),
                        expense.owner.id.eq(appUser.userId()))
                .groupBy(category.id)
                .fetch();
    }

    @Override
    public List<CategoryBudget> getBudgetsPerCategory(AppUser appUser) {
        LocalDate localDate = LocalDate.now();

        return jpaQueryFactory.selectFrom(categoryBudget)
                .join(budget).on(categoryBudget.budget.eq(budget))
                .where(
                        eqBudgetMonth(localDate),
                        budget.user.id.eq(appUser.userId())
                ).fetch();

    }

    private static BooleanExpression eqBudgetMonth(LocalDate localDate) {
        return budget.yearMonth.month.eq(LocalDate.of(localDate.getYear(), localDate.getMonth(), 1));
    }


    private static Predicate eqUser(long userId) {
        return expense.owner.id.eq(userId);
    }

    public List<CategoryExpenseSum> totalSumOfCategoriesExpense(AppUser appUser, ExpenseSearchCondition condition) {
        return jpaQueryFactory
                .select(Projections.fields(CategoryExpenseSum.class,
                        expense.category.id.as("categoryId"),
                        expense.money.amount.sum().as("totalExpenseAmount")))
                .from(expense)
                .where(
                        eqUser(appUser.userId()),
                        betweenAmounts(condition.getMin(), condition.getMax()),
                        inCategories(condition.getCategoryIds().getIds()),
                        betweenDate(condition.getStart(), condition.getEnd())
                )
                .groupBy(expense.category.id)
                .fetch();
    }

    private Predicate betweenDate(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return expense.expenseAt.between(start, end);
        }

        if (start == null && end != null) {
            return expense.expenseAt.before(end);
        }

        if (start != null) {
            return expense.expenseAt.after(start);
        }

        return expense.expenseAt.after(LocalDateTime.now().minusMonths(1));
    }

    private Predicate inCategories(List<Long> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return expense.category.id.in(categories);
    }

    private Predicate betweenAmounts(Money min, Money max) {
        if (min == null && max != null) {
            return expense.money.amount.loe(max.getAmount());
        } else if (min != null && max == null) {
            return expense.money.amount.goe(min.getAmount());
        } else if (min == null) {
            return null;
        } else {
            return expense.money.amount.between(min.getAmount(),
                    max.getAmount());
        }
    }

    private OrderSpecifier<?> findCriteria(ExpenseSearchCondition condition) {
        if (condition.getSort() == SearchSort.AMOUNT) {
            return expense.money.amount.desc();
        }
        if (condition.getSort() == SearchSort.CREATED) {
            return expense.createdAt.desc();
        }
        return expense.expenseAt.desc();
    }
}
