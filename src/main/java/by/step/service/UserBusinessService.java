package by.step.service;

import by.step.dto.UserDto;
import by.step.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;

public interface UserBusinessService {

    UserDto getUserById(Long id);

    UserDto getUserByUsername(String username);

    List<UserDto> getAllUsers();

    List<UserDto> getUsersByRole(UserRole role);

    void addToBalance(Long userId, BigDecimal amount);
}