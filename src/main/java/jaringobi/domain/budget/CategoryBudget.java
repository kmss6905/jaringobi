package jaringobi.domain.budget;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jaringobi.domain.BaseTimeEntity;
import jaringobi.exception.budget.InvalidBudgetException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "budget_by_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryBudget extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", foreignKey = @ForeignKey(name = "FK_budget_by_category_to_budget"))
    private Budget budget;

    @Builder
    public CategoryBudget(final Money amount, final Long categoryId, Budget budget) {
        validateBudget(amount, categoryId);
        this.budget = budget;
        this.amount = amount;
        this.categoryId = categoryId;
    }

    private void validateBudget(Money amount, Long categoryId) {
        if (Objects.isNull(amount) || Objects.isNull(categoryId)) {
            throw new InvalidBudgetException();
        }
    }

    public static CategoryBudget withBudget(Budget budget, CategoryBudget categoryBudget) {
        return CategoryBudget.builder()
                .categoryId(categoryBudget.categoryId)
                .amount(categoryBudget.amount)
                .budget(budget)
                .build();
    }

    public Long getId() {
        return id;
    }

    public Money getAmount() {
        return amount;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
