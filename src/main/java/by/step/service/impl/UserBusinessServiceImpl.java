package by.step.service.impl;

import by.step.client.DataServiceClient;
import by.step.dto.RegistrationRequestDto;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.UserBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Реализация бизнес-сервиса для работы с пользователями.
 * Выступает посредником между веб-слоем и data-service,
 * добавляя кэширование и агрегирующую бизнес-логику.
 *
 * @author Skin Market Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBusinessServiceImpl implements UserBusinessService {

    private final DataServiceClient dataServiceClient;

    /**
     * Получает пользователя по идентификатору с использованием кэша.
     *
     * @param id идентификатор пользователя
     * @return пользователь
     * @throws RuntimeException если пользователь не найден или data-service недоступен
     */
    @Override
    @Cacheable(value = "businessUserById", key = "#id")
    public UserDto getUserById(Long id) {
        log.info("Getting user by Id: id={}", id);
        try {
            UserDto user = dataServiceClient.getUserById(id);
            log.info("Found user: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Error getting user by id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to get user by id: " + id, e);
        }
    }

    /**
     * Получает пользователя по имени пользователя с использованием кэша.
     *
     * @param username имя пользователя (логин)
     * @return пользователь
     * @throws RuntimeException если пользователь не найден или data-service недоступен
     */
    @Override
    @Cacheable(value = "businessUserByUsername", key = "#username")
    public UserDto getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        try {
            return dataServiceClient.getUserByUsername(username);
        } catch (Exception e) {
            log.error("Error getting user by username {}: {}", username, e.getMessage());
            throw new RuntimeException("Failed to get user by username: " + username, e);
        }
    }

    /**
     * Получает данные для регистрации пользователя и отправки в data service
     *
     * @return registerUser
     */
    @Override
    public UserDto registerUser(RegistrationRequestDto request) {
        log.info("Registering user: username={}, email={}", request.getUsername(), request.getEmail());

        return dataServiceClient.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                UserRole.valueOf(request.getRole())
        );
    }

    /**
     * Получает список всех пользователей с использованием кэша.
     *
     * @return список всех пользователей
     * @throws RuntimeException если data-service недоступен
     */
    @Override
    @Cacheable(value = "businessAllUsers")
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        try {
            return dataServiceClient.getAllUsers();
        } catch (Exception e) {
            log.error("Error getting all users: {}", e.getMessage());
            throw new RuntimeException("Failed to get all users", e);
        }
    }

    /**
     * Получает пользователей по роли.
     * После получения очищает кэш для этой роли.
     *
     * @param role роль пользователя (USER, ARTIST, ADMIN, STUDIO)
     * @return список пользователей с указанной ролью
     * @throws RuntimeException если data-service недоступен
     */
    @Override
    @CacheEvict(value = "businessUserByRole", key = "#role")
    public List<UserDto> getUsersByRole(UserRole role) {
        log.info("Getting users by role: role={}", role);
        try {
            return dataServiceClient.getUsersByRole(role);
        } catch (Exception e) {
            log.error("Error getting users by role {}: {}", role, e.getMessage());
            throw new RuntimeException("Failed to get users by role: " + role, e);
        }
    }

    /**
     * Добавляет средства на баланс пользователя.
     * После пополнения очищает кэши пользователей.
     *
     * @param userId идентификатор пользователя
     * @param amount сумма пополнения
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"businessUserById", "businessUserByUsername", "businessAllUsers"}, allEntries = true)
    public void addToBalance(Long userId, BigDecimal amount) {
        log.info("Adding to balance: userId={}, amount={}", userId, amount);
        try {
            dataServiceClient.addToBalance(userId, amount);
        } catch (Exception e) {
            log.error("Error adding to balance: {}", e.getMessage());
            throw new RuntimeException("Failed to add to balance", e);
        }
    }

    /**
     * Списывает средства с баланса пользователя.
     * После списания очищает кэши пользователей.
     *
     * @param userId идентификатор пользователя
     * @param amount сумма списания
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"businessUserById", "businessUserByUsername", "businessAllUsers"}, allEntries = true)
    public void subtractFromBalance(Long userId, BigDecimal amount) {
        log.info("Subtracting from balance: userId={}, amount={}", userId, amount);
        try {
            dataServiceClient.subtractFromBalance(userId, amount);
        } catch (Exception e) {
            log.error("Error subtracting from balance: {}", e.getMessage());
            throw new RuntimeException("Failed to subtract from balance", e);
        }
    }

    /**
     * Обновляет баланс пользователя.
     *
     * @param userId идентификатор пользователя
     * @param newBalance новый баланс
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    public void updateBalance(Long userId, BigDecimal newBalance) {
        log.info("Updating balance: userId={}, newBalance={}", userId, newBalance);
        try {
            dataServiceClient.updateBalance(userId, newBalance);
        } catch (Exception e) {
            log.error("Error updating balance: {}", e.getMessage());
            throw new RuntimeException("Failed to update balance", e);
        }
    }

    /**
     * Проверяет, достаточно ли средств на балансе пользователя.
     *
     * @param userId идентификатор пользователя
     * @param amount проверяемая сумма
     * @return true если средств достаточно, false в противном случае
     */
    @Override
    public boolean hasEnoughBalance(Long userId, BigDecimal amount) {
        log.info("Checking balance: userId={}, amount={}", userId, amount);
        try {
            return dataServiceClient.hasEnoughBalance(userId, amount);
        } catch (Exception e) {
            log.error("Error checking balance: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Обновляет роль пользователя.
     *
     * @param userId идентификатор пользователя
     * @param role новая роль (USER, ARTIST, ADMIN, STUDIO)
     * @return пользователь с обновлённой ролью
     */
    @Override
    public UserDto updateUserRole(Long userId, String role) {
        log.info("Updating user role: userId={}, role={}", userId, role);
        return dataServiceClient.updateUserRole(userId, role);
    }

    /**
     * Получает пользователей по роли с пагинацией.
     *
     * @param role роль пользователя
     * @param pageable параметры пагинации
     * @return страница с пользователями указанной роли
     */
    @Override
    public Page<UserDto> getUsersByRolePage(UserRole role, Pageable pageable) {
        log.info("Получение пользователей по роли {} с пагинацией: page={}, size={}",
                role, pageable.getPageNumber(), pageable.getPageSize());
        return dataServiceClient.getUsersByRolePage(role, pageable);
    }

    /**
     * Получает всех пользователей с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница со всеми пользователями
     */
    @Override
    public Page<UserDto> getAllUsersPage(Pageable pageable) {
        log.info("Получение всех пользователей с пагинацией: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return dataServiceClient.getAllUsersPage(pageable);
    }
}