package by.step.client;

import by.step.dto.UserDto;
import by.step.enums.UserRole;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = "data-service.url=http://localhost:${wiremock.server.port}")
@DisplayName("WireMock тесты для DataServiceClient")
class DataServiceClientWireMockTest {

    @Autowired
    private DataServiceClient dataServiceClient;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(UserRole.USER)
                .balance(BigDecimal.valueOf(1000))
                .registeredAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    void getUserById_Success() {
        stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id": 1,
                                    "username": "testuser",
                                    "email": "test@example.com",
                                    "role": "USER",
                                    "balance": 1000,
                                    "registeredAt": "2024-01-01T10:00:00"
                                }
                                """)));

        UserDto result = dataServiceClient.getUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");

        verify(getRequestedFor(urlEqualTo("/api/users/1")));
    }

    @Test
    @DisplayName("Получение пользователя по ID - 404")
    void getUserById_NotFound() {
        stubFor(get(urlEqualTo("/api/users/999"))
                .willReturn(aResponse()
                        .withStatus(404)));

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            dataServiceClient.getUserById(999L);
        });
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_Success() {
        stubFor(get(urlEqualTo("/api/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "id": 1,
                                        "username": "user1",
                                        "email": "user1@example.com",
                                        "role": "USER",
                                        "balance": 1000
                                    },
                                    {
                                        "id": 2,
                                        "username": "artist1",
                                        "email": "artist1@example.com",
                                        "role": "ARTIST",
                                        "balance": 500
                                    }
                                ]
                                """)));

        var result = dataServiceClient.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("artist1");
    }

    @Test
    @DisplayName("Пополнение баланса")
    void addToBalance_Success() {
        stubFor(post(urlEqualTo("/api/users/1/balance/add?amount=500"))
                .willReturn(aResponse()
                        .withStatus(200)));

        dataServiceClient.addToBalance(1L, BigDecimal.valueOf(500));

        verify(postRequestedFor(urlEqualTo("/api/users/1/balance/add?amount=500")));
    }
}