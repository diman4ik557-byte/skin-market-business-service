package by.step.service;

import by.step.client.DataServiceClient;
import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import by.step.service.impl.OrderBusinessServiceImpl;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderBusinessServiceTest {

    @Mock
    private DataServiceClient dataServiceClient;

    @InjectMocks
    private OrderBusinessServiceImpl orderBusinessService;

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
    void createOrder_delegatesToClient() {
        when(dataServiceClient.createOrder(anyLong(), anyLong(), anyString(), any(BigDecimal.class)))
                .thenReturn(testOrderDto);

        OrderDto result = orderBusinessService.createOrder(createRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(dataServiceClient, times(1)).createOrder(1L,
                2L, "Test order", BigDecimal.valueOf(1000));
    }

    @Test
    void getOrder_delegatesToClient() {
        when(dataServiceClient.getOrderById(1L)).thenReturn(testOrderDto);

        OrderDto result = orderBusinessService.getOrder(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(dataServiceClient, times(1)).getOrderById(1L);
    }

    @Test
    void getOrdersByCustomer_delegatesToClient() {
        List<OrderDto> orders = Collections.singletonList(testOrderDto);
        when(dataServiceClient.getOrdersByCustomer(1L)).thenReturn(orders);

        List<OrderDto> result = orderBusinessService.getOrdersByCustomer(1L);

        assertThat(result).hasSize(1);
        verify(dataServiceClient, times(1)).getOrdersByCustomer(1L);
    }

    @Test
    void getOrdersByArtist_delegatesToClient() {
        List<OrderDto> orders = Collections.singletonList(testOrderDto);
        when(dataServiceClient.getOrdersByArtist(2L)).thenReturn(orders);

        List<OrderDto> result = orderBusinessService.getOrdersByArtist(2L);

        assertThat(result).hasSize(1);
        verify(dataServiceClient, times(1)).getOrdersByArtist(2L);
    }

    @Test
    void updateOrderStatus_delegatesToClient() {
        doNothing().when(dataServiceClient).updateOrderStatus(1L, OrderStatus.COMPLETED);

        orderBusinessService.updateOrderStatus(1L, OrderStatus.COMPLETED);

        verify(dataServiceClient, times(1)).updateOrderStatus(1L, OrderStatus.COMPLETED);
    }

    @Test
    void calculateTotalSpent_returnsSumOfCompletedOrders() {
        OrderDto completed1 = OrderDto.builder().price(BigDecimal.valueOf(100)).status(OrderStatus.COMPLETED).build();
        OrderDto completed2 = OrderDto.builder().price(BigDecimal.valueOf(200)).status(OrderStatus.COMPLETED).build();
        OrderDto inProgress = OrderDto.builder().price(BigDecimal.valueOf(300)).status(OrderStatus.IN_PROGRESS).build();

        when(dataServiceClient.getOrdersByCustomer(1L)).thenReturn(Arrays.asList(completed1,
                completed2, inProgress));

        BigDecimal total = orderBusinessService.calculateTotalSpent(1L);

        assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void getCompletedOrdersCount_returnsCountOfCompletedOrders() {
        OrderDto completed1 = OrderDto.builder().status(OrderStatus.COMPLETED).build();
        OrderDto completed2 = OrderDto.builder().status(OrderStatus.COMPLETED).build();
        OrderDto inProgress = OrderDto.builder().status(OrderStatus.IN_PROGRESS).build();

        when(dataServiceClient.getOrdersByArtist(2L)).thenReturn(Arrays.asList(completed1,
                completed2, inProgress));

        long count = orderBusinessService.getCompletedOrdersCount(2L);

        assertThat(count).isEqualTo(2);
    }
}
