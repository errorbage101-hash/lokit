package com.elshoura.lokit.controllers;

import com.elshoura.lokit.models.dto.request.ChangePasswordRequest;
import com.elshoura.lokit.models.dto.request.UpdateAccountRequest;
import com.elshoura.lokit.models.dto.response.AccountResponse;
import com.elshoura.lokit.security.CustomUserDetails;
import com.elshoura.lokit.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "User account management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Get current user account")
    @GetMapping
    public ResponseEntity<AccountResponse> getMyAccountApi(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(accountService.getMyAccount(userId));
    }

    @Operation(summary = "Update current user account")
    @PutMapping
    public ResponseEntity<AccountResponse> updateMyAccountApi(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateAccountRequest request
    ) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(accountService.updateMyAccount(userId, request));
    }

    @Operation(summary = "Change current user password")
    @PutMapping ("/password")
    public ResponseEntity<Void> changePasswordApi(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long userId = userDetails.getUser().getId();

        accountService.changePassword(userId, request);

        return ResponseEntity.noContent().build();
    }
}
