package jaringobi.service;

import jaringobi.domain.budget.Budget;
import jaringobi.domain.budget.BudgetRepository;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
import jaringobi.dto.request.AddBudgetRequest;
import jaringobi.dto.request.BudgetByCategoryRequest;
import jaringobi.dto.request.ModifyBudgetCategory;
import jaringobi.dto.response.AddBudgetResponse;
import jaringobi.dto.response.BudgetResponse;
import jaringobi.exception.auth.NoPermissionException;
import jaringobi.exception.budget.BudgetNotFoundException;
import jaringobi.exception.user.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddBudgetResponse addBudget(AppUser appUser, AddBudgetRequest addBudgetRequest) {
        final User user = findUser(appUser);
        Budget budget = addBudgetRequest.toBudget();
        List<CategoryBudget> categoryBudgets = addBudgetRequest.categoryBudgets();
        budget.setUser(user);
        budget.setCategoryBudgets(categoryBudgets);
        Budget savedBudget = budgetRepository.save(budget);
        return AddBudgetResponse.of(savedBudget);
    }

    @Transactional
    public void deleteBudget(AppUser appUser, long budgetId) {
        Budget budget = findBudgetOwnerOf(appUser, budgetId);
        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public BudgetResponse findOneBudget(AppUser appUser, long budgetId) {
        Budget budget = findBudgetOwnerOf(appUser, budgetId);
        return BudgetResponse.of(budget);
    }

    @Transactional
    public void addBudgetCategory(AppUser appUser, long budgetId, BudgetByCategoryRequest budgetByCategoryRequest) {
        Budget budget = findBudgetOwnerOf(appUser, budgetId);
        budget.addBudgetCategory(budgetByCategoryRequest.toCategoryBudget());
    }

    @Transactional
    public void modifyBudgetCategory(AppUser appUser, long budgetId, long budgetCategoryId, ModifyBudgetCategory modifyBudgetCategory) {
        Budget budget = findBudgetOwnerOf(appUser, budgetId);
        CategoryBudget budgetCategory = modifyBudgetCategory.toCategoryBudgetWithCategory(budgetCategoryId);
        budget.modifyBudgetCategory(budgetCategory);
    }

    private Budget findBudgetOwnerOf(AppUser appUser, long budgetId) {
        Budget budget = findBudget(budgetId);
        if (!budget.isOwner(appUser)) {
            throw new NoPermissionException();
        }
        return budget;
    }

    private Budget findBudget(long budgetId) {
        return budgetRepository.findById(budgetId)
                .orElseThrow(BudgetNotFoundException::new);
    }


    private User findUser(AppUser appUser) {
        return userRepository.findById(appUser.userId())
                .orElseThrow(UserNotFoundException::new);
    }
}
