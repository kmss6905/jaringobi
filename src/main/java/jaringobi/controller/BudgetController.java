package jaringobi.controller;

import jakarta.validation.Valid;
import jaringobi.auth.AuthenticationPrincipal;
import jaringobi.dto.request.AddBudgetRequest;
import jaringobi.dto.response.AddBudgetResponse;
import jaringobi.domain.user.AppUser;
import jaringobi.service.BudgetService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Void> addBudget(
            @AuthenticationPrincipal AppUser appUser,
            @Valid @RequestBody AddBudgetRequest addBudgetRequest) {
        AddBudgetResponse addBudgetResponse = budgetService.addBudget(appUser, addBudgetRequest);
        return ResponseEntity.created(URI.create("/api/v1/budget/" + addBudgetResponse.getBudgetNo())).build();
    }

}
