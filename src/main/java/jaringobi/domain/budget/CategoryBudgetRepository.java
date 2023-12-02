package jaringobi.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget, Long> {

    Integer deleteByBudgetAndCategoryId(Budget budget, long categoryId);
}
