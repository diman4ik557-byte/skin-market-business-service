package by.step.controller;

import by.step.dto.ApiResponseDto;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.UserBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/business/users")
@RequiredArgsConstructor
public class UserController {

    private final UserBusinessService userBusinessService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUserById(@PathVariable Long id) {
        UserDto user = userBusinessService.getUserById(id);
        return ResponseEntity.ok(ApiResponseDto.success(user));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUserByUsername(@PathVariable String username) {
        UserDto user = userBusinessService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponseDto.success(user));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userBusinessService.getAllUsers();
        return ResponseEntity.ok(ApiResponseDto.success(users));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponseDto<List<UserDto>>> getUsersByRole(@PathVariable UserRole role) {
        List<UserDto> users = userBusinessService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponseDto.success(users));
    }

    @PostMapping("/{userId}/balance/add")
    public ResponseEntity<ApiResponseDto<Void>> addToBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        userBusinessService.addToBalance(userId, amount);
        return ResponseEntity.ok(ApiResponseDto.success("Balance added successfully", null));
    }

    @PostMapping("/{userId}/balance/subtract")
    public ResponseEntity<ApiResponseDto<Void>> subtractFromBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        userBusinessService.subtractFromBalance(userId, amount);
        return ResponseEntity.ok(ApiResponseDto.success("Balance subtracted successfully", null));
    }

    @PutMapping("/{userId}/balance")
    public ResponseEntity<ApiResponseDto<Void>> updateBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal newBalance) {
        userBusinessService.updateBalance(userId, newBalance);
        return ResponseEntity.ok(ApiResponseDto.success("Balance updated successfully", null));
    }

    @GetMapping("/{userId}/balance/sufficient")
    public ResponseEntity<ApiResponseDto<Boolean>> hasEnoughBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        boolean result = userBusinessService.hasEnoughBalance(userId, amount);
        return ResponseEntity.ok(ApiResponseDto.success(result));
    }
}