package by.step.integration;

import by.step.client.DataServiceClient;
import by.step.dto.OrderDto;
import by.step.dto.UserDto;
import by.step.enums.OrderStatus;
import by.step.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class DataServiceClientTest {

    @Autowired
    private DataServiceClient dataServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUserDto;
    private OrderDto testOrderDto;

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

        testOrderDto = OrderDto.builder()
                .id(1L)
                .customerId(1L)
                .customerName("ivan")
                .artistId(2L)
                .artistName("petr")
                .status(OrderStatus.IN_PROGRESS)
                .description("Test order")
                .price(BigDecimal.valueOf(1000))
                .createdAt(LocalDateTime.now())
                .build();

        stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(testUserDto))));

        stubFor(get(urlEqualTo("/api/users/username/ivan"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(testUserDto))));

        stubFor(get(urlEqualTo("/api/users"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(Collections.singletonList(testUserDto)))));

        stubFor(post(urlEqualTo("/api/orders?customerId=1&artistId=2&description=Test%20order&price=1000"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(testOrderDto))));

        stubFor(get(urlEqualTo("/api/orders/1"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(testOrderDto))));

        stubFor(put(urlEqualTo("/api/orders/1/status?status=COMPLETED"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));

        stubFor(post(urlEqualTo("/api/users/1/balance/add?amount=500"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));
    }

    private String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserDto result = dataServiceClient.getUserById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        UserDto result = dataServiceClient.getUserByUsername("ivan");
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("ivan");
    }

    @Test
    void getAllUsers_ShouldReturnUsers() {
        List<UserDto> result = dataServiceClient.getAllUsers();
        assertThat(result).hasSize(1);
    }

    @Test
    void createOrder_ShouldReturnOrder() {
        OrderDto result = dataServiceClient.createOrder(1L, 2L, "Test order", BigDecimal.valueOf(1000));
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        OrderDto result = dataServiceClient.getOrderById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void updateOrderStatus_ShouldSucceed() {
        dataServiceClient.updateOrderStatus(1L, OrderStatus.COMPLETED);
        verify(putRequestedFor(urlEqualTo("/api/orders/1/status?status=COMPLETED")));
    }

    @Test
    void addToBalance_ShouldSucceed() {
        dataServiceClient.addToBalance(1L, BigDecimal.valueOf(500));
        verify(postRequestedFor(urlEqualTo("/api/users/1/balance/add?amount=500")));
    }
}