package by.step.service;

import by.step.client.DataServiceClient;
import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.impl.UserBusinessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
}
