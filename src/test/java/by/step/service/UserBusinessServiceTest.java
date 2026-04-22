package by.step.service;

import by.step.client.DataServiceClient;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.impl.UserBusinessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBusinessServiceTest {

    @Mock
    private DataServiceClient dataServiceClient;

    @InjectMocks
    private UserBusinessServiceImpl userBusinessService;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .id(1L)
                .username("ivan")
                .email("ivan@example.com")
                .role(UserRole.USER)
                .balance(BigDecimal.valueOf(1000))
                .registeredAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserById_delegatesToClient() {
        when(dataServiceClient.getUserById(1L)).thenReturn(testUserDto);

        UserDto result = userBusinessService.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("ivan");
        verify(dataServiceClient, times(1)).getUserById(1L);
    }

    @Test
    void getUserByUsername_delegatesToClient() {
        when(dataServiceClient.getUserByUsername("ivan")).thenReturn(testUserDto);

        UserDto result = userBusinessService.getUserByUsername("ivan");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("ivan");
        verify(dataServiceClient, times(1)).getUserByUsername("ivan");
    }

    @Test
    void getAllUsers_delegatesToClient() {
        List<UserDto> users = Collections.singletonList(testUserDto);
        when(dataServiceClient.getAllUsers()).thenReturn(users);

        List<UserDto> result = userBusinessService.getAllUsers();

        assertThat(result).hasSize(1);
        verify(dataServiceClient, times(1)).getAllUsers();
    }

    @Test
    void getUsersByRole_delegatesToClient() {
        List<UserDto> users = Collections.singletonList(testUserDto);
        when(dataServiceClient.getUsersByRole(UserRole.USER)).thenReturn(users);

        List<UserDto> result = userBusinessService.getUsersByRole(UserRole.USER);

        assertThat(result).hasSize(1);
        verify(dataServiceClient, times(1)).getUsersByRole(UserRole.USER);
    }

    @Test
    void addToBalance_delegatesToClient() {
        doNothing().when(dataServiceClient).addToBalance(1L, BigDecimal.valueOf(500));

        userBusinessService.addToBalance(1L, BigDecimal.valueOf(500));

        verify(dataServiceClient, times(1)).addToBalance(1L, BigDecimal.valueOf(500));
    }

    @Test
    void subtractFromBalance_delegatesToClient() {
        doNothing().when(dataServiceClient).subtractFromBalance(1L, BigDecimal.valueOf(500));

        userBusinessService.subtractFromBalance(1L, BigDecimal.valueOf(500));

        verify(dataServiceClient, times(1)).subtractFromBalance(1L, BigDecimal.valueOf(500));
    }

    @Test
    void updateBalance_delegatesToClient() {
        doNothing().when(dataServiceClient).updateBalance(1L, BigDecimal.valueOf(2000));

        userBusinessService.updateBalance(1L, BigDecimal.valueOf(2000));

        verify(dataServiceClient, times(1)).updateBalance(1L, BigDecimal.valueOf(2000));
    }

    @Test
    void hasEnoughBalance_delegatesToClient() {
        when(dataServiceClient.hasEnoughBalance(1L, BigDecimal.valueOf(500))).thenReturn(true);

        boolean result = userBusinessService.hasEnoughBalance(1L, BigDecimal.valueOf(500));

        assertThat(result).isTrue();
        verify(dataServiceClient, times(1)).hasEnoughBalance(1L, BigDecimal.valueOf(500));
    }

    //  дополнительные тесты

    @Test
    @DisplayName("Обновление роли пользователя - успех")
    void updateUserRole_ShouldDelegateToClient() {
        UserDto updatedUser = UserDto.builder()
                .id(1L)
                .username("ivan")
                .role(UserRole.ARTIST)
                .build();

        when(dataServiceClient.updateUserRole(1L, "ARTIST")).thenReturn(updatedUser);

        UserDto result = userBusinessService.updateUserRole(1L, "ARTIST");

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(UserRole.ARTIST);
        verify(dataServiceClient, times(1)).updateUserRole(1L, "ARTIST");
    }

    @Test
    @DisplayName("Получение пользователей по роли с пагинацией - успех")
    void getUsersByRolePage_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());
        Page<UserDto> expectedPage = new PageImpl<>(List.of(testUserDto), pageable, 1);

        when(dataServiceClient.getUsersByRolePage(UserRole.USER, pageable)).thenReturn(expectedPage);

        Page<UserDto> result = userBusinessService.getUsersByRolePage(UserRole.USER, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("ivan");
        verify(dataServiceClient, times(1)).getUsersByRolePage(UserRole.USER, pageable);
    }

    @Test
    @DisplayName("Получение всех пользователей с пагинацией - успех")
    void getAllUsersPage_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());
        Page<UserDto> expectedPage = new PageImpl<>(List.of(testUserDto), pageable, 1);

        when(dataServiceClient.getAllUsersPage(pageable)).thenReturn(expectedPage);

        Page<UserDto> result = userBusinessService.getAllUsersPage(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(dataServiceClient, times(1)).getAllUsersPage(pageable);
    }

// тесты на ошибки

    @Test
    @DisplayName("Получение пользователя по ID - ошибка API")
    void getUserById_WhenException_ThrowsRuntimeException() {
        when(dataServiceClient.getUserById(1L)).thenThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> userBusinessService.getUserById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to get user by id");
    }

    @Test
    @DisplayName("Получение пользователя по имени - ошибка API")
    void getUserByUsername_WhenException_ThrowsRuntimeException() {
        when(dataServiceClient.getUserByUsername("ivan")).thenThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> userBusinessService.getUserByUsername("ivan"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to get user by username");
    }

    @Test
    @DisplayName("Получение всех пользователей - ошибка API")
    void getAllUsers_WhenException_ThrowsRuntimeException() {
        when(dataServiceClient.getAllUsers()).thenThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> userBusinessService.getAllUsers())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to get all users");
    }

    @Test
    @DisplayName("Получение пользователей по роли - ошибка API")
    void getUsersByRole_WhenException_ThrowsRuntimeException() {
        when(dataServiceClient.getUsersByRole(UserRole.USER)).thenThrow(new RuntimeException("API Error"));

        assertThatThrownBy(() -> userBusinessService.getUsersByRole(UserRole.USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to get users by role");
    }

    @Test
    @DisplayName("Пополнение баланса - ошибка API")
    void addToBalance_WhenException_ThrowsRuntimeException() {
        doThrow(new RuntimeException("API Error")).when(dataServiceClient).addToBalance(1L, BigDecimal.valueOf(500));

        assertThatThrownBy(() -> userBusinessService.addToBalance(1L, BigDecimal.valueOf(500)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to add to balance");
    }

    @Test
    @DisplayName("Списание с баланса - ошибка API")
    void subtractFromBalance_WhenException_ThrowsRuntimeException() {
        doThrow(new RuntimeException("API Error")).when(dataServiceClient).subtractFromBalance(1L, BigDecimal.valueOf(500));

        assertThatThrownBy(() -> userBusinessService.subtractFromBalance(1L, BigDecimal.valueOf(500)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to subtract from balance");
    }

    @Test
    @DisplayName("Обновление баланса - ошибка API")
    void updateBalance_WhenException_ThrowsRuntimeException() {
        doThrow(new RuntimeException("API Error")).when(dataServiceClient).updateBalance(1L, BigDecimal.valueOf(2000));

        assertThatThrownBy(() -> userBusinessService.updateBalance(1L, BigDecimal.valueOf(2000)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update balance");
    }

    @Test
    @DisplayName("Проверка достаточности баланса - ошибка API, возвращает false")
    void hasEnoughBalance_WhenException_ReturnsFalse() {
        when(dataServiceClient.hasEnoughBalance(1L, BigDecimal.valueOf(500))).thenThrow(new RuntimeException("API Error"));

        boolean result = userBusinessService.hasEnoughBalance(1L, BigDecimal.valueOf(500));

        assertThat(result).isFalse();
    }
}