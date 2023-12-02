package jaringobi.domain.budget;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jaringobi.domain.BaseTimeEntity;
import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.exception.budget.BudgetCategoryDuplicatedException;
import jaringobi.exception.budget.BudgetCategoryNotFoundException;
import jaringobi.exception.budget.InvalidBudgetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budget")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "budget_month", nullable = false)
    private BudgetYearMonth yearMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_budget_to_user"))
    private User user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<CategoryBudget> categoryBudgets = new ArrayList<>();

    @Builder
    public Budget(final Long id, final BudgetYearMonth yearMonth, final User user,
            final List<CategoryBudget> categoryBudgets) {
        verifyYearNonEmpty(yearMonth);
        this.id = id;
        this.yearMonth = yearMonth;
        if (Objects.nonNull(user)) {
            setUser(user);
        }

        if (Objects.nonNull(categoryBudgets)) {
            setCategoryBudgets(categoryBudgets);
        }
    }

    private void verifyYearNonEmpty(BudgetYearMonth yearMonth) {
        if (Objects.isNull(yearMonth) || yearMonth.isEmptyMonth()) {
            throw new InvalidBudgetException();
        }
    }

    public void setCategoryBudgets(List<CategoryBudget> categoryBudgets) {
        if (!this.categoryBudgets.isEmpty()) {
            throw new InvalidBudgetException();
        }
        this.categoryBudgets = categoryBudgets.stream()
                .map(categoryBudget -> CategoryBudget.withBudget(this, categoryBudget))
                .collect(Collectors.toList());
    }

    public void setUser(User user) {
        if (Objects.nonNull(this.user)) {
            throw new InvalidBudgetException();
        }

        if (Objects.nonNull(user)) {
            this.user = user;
        }
    }

    public Long getId() {
        return id;
    }

    public BudgetYearMonth getYearMonth() {
        return yearMonth;
    }

    public List<CategoryBudget> getCategoryBudgets() {
        return categoryBudgets;
    }

    public boolean isOwner(AppUser appUser) {
        return this.user.isSame(appUser);
    }

    public void addBudgetCategory(CategoryBudget categoryBudget) {
        verifyNotDuplicatedCategory(categoryBudget);
        categoryBudgets.add(CategoryBudget.withBudget(this, categoryBudget));
    }

    private void verifyNotDuplicatedCategory(CategoryBudget categoryBudget) {
        boolean existed = categoryBudgets.stream()
                .anyMatch(it -> it.getCategoryId().equals(categoryBudget.getCategoryId()));
        if (existed) {
            throw new BudgetCategoryDuplicatedException();
        }
    }

    public void modifyBudgetCategory(CategoryBudget modifyCategoryBudget) {
        CategoryBudget categoryBudget = findBudgetCategory(modifyCategoryBudget);
        if (Objects.isNull(categoryBudget)) {
            throw new BudgetCategoryNotFoundException();
        }
        categoryBudget.modify(modifyCategoryBudget);
    }

    private CategoryBudget findBudgetCategory(CategoryBudget modifyBudgetCategory) {
        return categoryBudgets.stream()
                .filter(it -> it.getCategoryId().equals(modifyBudgetCategory.getCategoryId()))
                .findFirst()
                .orElse(null);
    }
}
