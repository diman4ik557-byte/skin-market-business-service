package by.step.controller;

import by.step.dto.UserDto;
import by.step.enums.UserRole;
import by.step.service.UserBusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserBusinessService userBusinessService;

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
    void getUserById_ShouldReturnUser() throws Exception {
        when(userBusinessService.getUserById(1L)).thenReturn(testUserDto);

        mockMvc.perform(get("/api/business/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("ivan"));
    }

    @Test
    void getUserByUsername_ShouldReturnUser() throws Exception {
        when(userBusinessService.getUserByUsername("ivan")).thenReturn(testUserDto);

        mockMvc.perform(get("/api/business/users/username/{username}", "ivan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("ivan"));
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        List<UserDto> users = Collections.singletonList(testUserDto);
        when(userBusinessService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/business/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void getUsersByRole_ShouldReturnUsers() throws Exception {
        List<UserDto> users = Collections.singletonList(testUserDto);
        when(userBusinessService.getUsersByRole(UserRole.USER)).thenReturn(users);

        mockMvc.perform(get("/api/business/users/role/{role}", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void addToBalance_ShouldReturnSuccess() throws Exception {
        doNothing().when(userBusinessService).addToBalance(1L, BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/business/users/{userId}/balance/add", 1L)
                        .with(csrf())
                        .param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void subtractFromBalance_ShouldReturnSuccess() throws Exception {
        doNothing().when(userBusinessService).subtractFromBalance(1L, BigDecimal.valueOf(500));

        mockMvc.perform(post("/api/business/users/{userId}/balance/subtract", 1L)
                        .with(csrf())
                        .param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateBalance_ShouldReturnSuccess() throws Exception {
        doNothing().when(userBusinessService).updateBalance(1L, BigDecimal.valueOf(2000));

        mockMvc.perform(put("/api/business/users/{userId}/balance", 1L)
                        .with(csrf())
                        .param("newBalance", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void hasEnoughBalance_ShouldReturnTrue() throws Exception {
        when(userBusinessService.hasEnoughBalance(1L, BigDecimal.valueOf(500))).thenReturn(true);

        mockMvc.perform(get("/api/business/users/{userId}/balance/sufficient", 1L)
                        .param("amount", "500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
}