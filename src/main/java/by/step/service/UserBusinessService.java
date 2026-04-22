package by.step.service;

import by.step.dto.RegistrationRequestDto;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface UserBusinessService {

    UserDto getUserById(Long id);

    UserDto getUserByUsername(String username);

    UserDto registerUser(RegistrationRequestDto request);

    List<UserDto> getAllUsers();

    List<UserDto> getUsersByRole(UserRole role);

    void addToBalance(Long userId, BigDecimal amount);

    void subtractFromBalance(Long userId, BigDecimal amount);

    void updateBalance(Long userId, BigDecimal newBalance);

    boolean hasEnoughBalance(Long userId, BigDecimal amount);

    UserDto updateUserRole(Long userId, String role);

    Page<UserDto> getUsersByRolePage(UserRole role, Pageable pageable);

    Page<UserDto> getAllUsersPage(Pageable pageable);
}