package by.step.service.impl;

import by.step.client.DataServiceClient;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.UserBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBusinessServiceImpl implements UserBusinessService {

    private final DataServiceClient dataServiceClient;

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя по Id: id={}", id);
        return dataServiceClient.getUserById(id);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        log.info("Получение пользователя по имени: username={}", username);
        return dataServiceClient.getUserByUsername(username);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение всех пользователе2");
        return dataServiceClient.getAllUsers();
    }

    @Override
    public List<UserDto> getUsersByRole(UserRole role) {
        log.info("Получение пользователей по ролям: role={}", role);
        return dataServiceClient.getUsersByRole(role);
    }

    @Override
    public void addToBalance(Long userId, BigDecimal amount) {
        log.info("Пополнение баланса: userId={}, amount={}", userId, amount);
        dataServiceClient.addToBalance(userId, amount);
    }

    @Override
    public void subtractFromBalance(Long userId, BigDecimal amount) {
        log.info("Снятие баланса: userId={}, amount={}", userId, amount);
        dataServiceClient.subtractFromBalance(userId, amount);
    }

    @Override
    public void updateBalance(Long userId, BigDecimal newBalance) {
        log.info("Обновление баланса: userId={}, newBalance={}", userId, newBalance);
        dataServiceClient.updateBalance(userId, newBalance);
    }

    @Override
    public boolean hasEnoughBalance(Long userId, BigDecimal amount) {
        log.info("Проверка баланса: userId={}, amount={}", userId, amount);
        return dataServiceClient.hasEnoughBalance(userId, amount);
    }
}
