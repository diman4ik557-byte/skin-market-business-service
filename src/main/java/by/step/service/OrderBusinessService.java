package by.step.service;

import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderBusinessService {

    OrderDto createOrder(CreateOrderRequestDto request);

    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrdersByCustomer(Long customerId);

    List<OrderDto> getOrdersByArtist(Long artistId);

    List<OrderDto> getOrdersByStatus(OrderStatus status);

    void updateOrderStatus(Long orderId, OrderStatus status);

    void updateFinalFile(Long orderId, String fileUrl);

    void startOrder(Long orderId);

    void completeOrder(Long orderId);

    void cancelOrder(Long orderId);

    void submitForReview(Long orderId, String finalFileUrl);

    BigDecimal getArtistEarnings(Long artistId);

    BigDecimal calculateTotalSpent(Long customerId);

    long getCompletedOrdersCount(Long artistId);

    Page<OrderDto> getOrdersByCustomerPage(Long customerId, Pageable pageable);

    Page<OrderDto> getOrdersByArtistPage(Long artistId, Pageable pageable);
}
