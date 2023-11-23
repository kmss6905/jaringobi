package jaringobi.service;

import jaringobi.dto.request.AddBudgetRequest;
import jaringobi.dto.response.AddBudgetResponse;
import jaringobi.domain.budget.Budget;
import jaringobi.domain.budget.BudgetRepository;
import jaringobi.domain.budget.CategoryBudget;
import jaringobi.domain.user.AppUser;
import jaringobi.domain.user.User;
import jaringobi.domain.user.UserRepository;
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


    private User findUser(AppUser appUser) {
        return userRepository.findById(appUser.userId())
                .orElseThrow(UserNotFoundException::new);
    }
}
