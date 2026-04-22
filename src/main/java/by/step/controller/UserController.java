package by.step.controller;

import by.step.dto.ApiResponseDto;
import by.step.dto.RegistrationRequestDto;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.UserBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с пользователями.
 * Является частью бизнес-сервиса и выступает посредником между веб-интерфейсом и дата-сервисом.
 *
 * @author Skin Market Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/business/users")
@RequiredArgsConstructor
public class UserController {

    private final UserBusinessService userBusinessService;

    /**
     * Получает пользователя по ID.
     *
     * @param id ID пользователя
     * @return ApiResponseDto с пользователем
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUserById(@PathVariable Long id) {
        log.debug("REST запрос: получение пользователя по id={}", id);
        UserDto user = userBusinessService.getUserById(id);
        return ResponseEntity.ok(ApiResponseDto.success(user));
    }

    /**
     * Получает пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return ApiResponseDto с пользователем
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponseDto<UserDto>> getUserByUsername(@PathVariable String username) {
        log.debug("REST запрос: получение пользователя по username={}", username);
        UserDto user = userBusinessService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponseDto.success(user));
    }

    /**
     * Получает список всех пользователей.
     *
     * @return ApiResponseDto со списком пользователей
     */
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<UserDto>>> getAllUsers() {
        log.debug("REST запрос: получение всех пользователей");
        List<UserDto> users = userBusinessService.getAllUsers();
        return ResponseEntity.ok(ApiResponseDto.success(users));
    }

    /**
     * Получает пользователей по роли.
     *
     * @param role роль пользователя (USER, ARTIST, ADMIN, STUDIO)
     * @return ApiResponseDto со списком пользователей
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponseDto<List<UserDto>>> getUsersByRole(@PathVariable UserRole role) {
        log.debug("REST запрос: получение пользователей по роли {}", role);
        List<UserDto> users = userBusinessService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponseDto.success(users));
    }

    /**
     * Добавляет средства на баланс пользователя.
     *
     * @param userId ID пользователя
     * @param amount сумма пополнения
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{userId}/balance/add")
    public ResponseEntity<ApiResponseDto<Void>> addToBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        log.info("REST запрос: пополнение баланса пользователя {} на {}", userId, amount);
        userBusinessService.addToBalance(userId, amount);
        return ResponseEntity.ok(ApiResponseDto.success("Баланс пополнен", null));
    }

    /**
     * Списывает средства с баланса пользователя.
     *
     * @param userId ID пользователя
     * @param amount сумма списания
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{userId}/balance/subtract")
    public ResponseEntity<ApiResponseDto<Void>> subtractFromBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        log.info("REST запрос: списание с баланса пользователя {} суммы {}", userId, amount);
        userBusinessService.subtractFromBalance(userId, amount);
        return ResponseEntity.ok(ApiResponseDto.success("Средства списаны", null));
    }

    /**
     * Обновляет баланс пользователя.
     *
     * @param userId ID пользователя
     * @param newBalance новый баланс
     * @return ApiResponseDto с подтверждением
     */
    @PutMapping("/{userId}/balance")
    public ResponseEntity<ApiResponseDto<Void>> updateBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal newBalance) {
        log.info("REST запрос: обновление баланса пользователя {} до {}", userId, newBalance);
        userBusinessService.updateBalance(userId, newBalance);
        return ResponseEntity.ok(ApiResponseDto.success("Баланс обновлён", null));
    }

    /**
     * Проверяет, достаточно ли средств на балансе пользователя.
     *
     * @param userId ID пользователя
     * @param amount проверяемая сумма
     * @return ApiResponseDto с результатом проверки
     */
    @GetMapping("/{userId}/balance/sufficient")
    public ResponseEntity<ApiResponseDto<Boolean>> hasEnoughBalance(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        log.debug("REST запрос: проверка баланса пользователя {} на сумму {}", userId, amount);
        boolean result = userBusinessService.hasEnoughBalance(userId, amount);
        return ResponseEntity.ok(ApiResponseDto.success(result));
    }

    /**
     * Обновляет роль пользователя.
     *
     * @param userId ID пользователя
     * @param role новая роль (USER, ARTIST, ADMIN, STUDIO)
     * @return ApiResponseDto с обновленным пользователем
     */
    @PutMapping("/{userId}/role")
    public ResponseEntity<ApiResponseDto<UserDto>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        log.info("REST запрос: обновление роли пользователя {} на {}", userId, role);
        UserDto user = userBusinessService.updateUserRole(userId, role);
        return ResponseEntity.ok(ApiResponseDto.success("Роль обновлена", user));
    }

    /**
     * Получает пользователей по роли с пагинацией.
     *
     * @param role роль пользователя
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей пользователей
     */
    @GetMapping("/role/{role}/page")
    public ResponseEntity<ApiResponseDto<Page<UserDto>>> getUsersByRolePage(
            @PathVariable UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<UserDto> users = userBusinessService.getUsersByRolePage(role, pageable);
        return ResponseEntity.ok(ApiResponseDto.success(users));
    }

    /**
     * Получает всех пользователей с пагинацией.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей пользователей
     */
    @GetMapping("/page")
    public ResponseEntity<ApiResponseDto<Page<UserDto>>> getAllUsersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<UserDto> users = userBusinessService.getAllUsersPage(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(users));
    }

    /**
     * Регистрирует нового пользователя в системе (создаёт в data-service).
     *
     * @param request DTO с данными пользователя
     * @return ApiResponseDto с созданным пользователем
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserDto>> registerUser(@RequestBody RegistrationRequestDto request) {
        log.info("REST запрос: регистрация пользователя username={}, email={}, role={}",
                request.getUsername(), request.getEmail(), request.getRole());

        UserDto user = userBusinessService.registerUser(request);
        return ResponseEntity.ok(ApiResponseDto.success("Пользователь зарегистрирован", user));
    }
}