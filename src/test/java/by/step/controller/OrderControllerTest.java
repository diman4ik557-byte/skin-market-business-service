package by.step.controller;

import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import by.step.service.OrderBusinessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderBusinessService orderBusinessService;

    private OrderDto testOrderDto;
    private CreateOrderRequestDto createRequest;

    @BeforeEach
    void setUp() {
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

        createRequest = CreateOrderRequestDto.builder()
                .customerId(1L)
                .artistId(2L)
                .description("Test order")
                .price(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        when(orderBusinessService.createOrder(any(CreateOrderRequestDto.class)))
                .thenReturn(testOrderDto);

        mockMvc.perform(post("/api/business/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.customerName").value("ivan"));
    }

    @Test
    void getOrder_ShouldReturnOrder() throws Exception {
        when(orderBusinessService.getOrder(1L)).thenReturn(testOrderDto);

        mockMvc.perform(get("/api/business/orders/{orderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.description").value("Test order"));
    }

    @Test
    void getOrdersByCustomer_ShouldReturnOrders() throws Exception {
        List<OrderDto> orders = Collections.singletonList(testOrderDto);
        when(orderBusinessService.getOrdersByCustomer(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/business/orders/customer/{customerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void getOrdersByArtist_ShouldReturnOrders() throws Exception {
        List<OrderDto> orders = Collections.singletonList(testOrderDto);
        when(orderBusinessService.getOrdersByArtist(2L)).thenReturn(orders);

        mockMvc.perform(get("/api/business/orders/artist/{artistId}", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void updateOrderStatus_ShouldReturnSuccess() throws Exception {
        doNothing().when(orderBusinessService).updateOrderStatus(1L, OrderStatus.COMPLETED);

        mockMvc.perform(put("/api/business/orders/{orderId}/status", 1L)
                        .with(csrf())
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void startOrder_ShouldReturnSuccess() throws Exception {
        doNothing().when(orderBusinessService).startOrder(1L);

        mockMvc.perform(post("/api/business/orders/{orderId}/start", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void completeOrder_ShouldReturnSuccess() throws Exception {
        doNothing().when(orderBusinessService).completeOrder(1L);

        mockMvc.perform(post("/api/business/orders/{orderId}/complete", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void cancelOrder_ShouldReturnSuccess() throws Exception {
        doNothing().when(orderBusinessService).cancelOrder(1L);

        mockMvc.perform(post("/api/business/orders/{orderId}/cancel", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getTotalSpent_ShouldReturnTotal() throws Exception {
        when(orderBusinessService.calculateTotalSpent(1L)).thenReturn(BigDecimal.valueOf(1500));

        mockMvc.perform(get("/api/business/orders/customer/{customerId}/total-spent", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(1500));
    }
}
