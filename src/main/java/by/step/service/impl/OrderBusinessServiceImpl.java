package by.step.service.impl;

import by.step.client.DataServiceClient;
import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import by.step.service.OrderBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderBusinessServiceImpl implements OrderBusinessService {

    private final DataServiceClient dataServiceClient;

    @Override
    public OrderDto createOrder(CreateOrderRequestDto request) {
        log.info("Creating order: customerId={}, artistId={}, price={}",
                request.getCustomerId(), request.getArtistId(), request.getPrice());

        return dataServiceClient.createOrder(
                request.getCustomerId(),
                request.getArtistId(),
                request.getDescription(),
                request.getPrice()
        );
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        log.info("Getting order: orderId={}", orderId);
        return dataServiceClient.getOrderById(orderId);
    }

    @Override
    public List<OrderDto> getOrdersByCustomer(Long customerId) {
        log.info("Getting orders by customer: customerId={}", customerId);
        return dataServiceClient.getOrdersByCustomer(customerId);
    }

    @Override
    public List<OrderDto> getOrdersByArtist(Long artistId) {
        log.info("Getting orders by artist: artistId={}", artistId);
        return dataServiceClient.getOrdersByArtist(artistId);
    }

    @Override
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        log.info("Getting orders by status: status={}", status);
        return dataServiceClient.getOrdersByStatus(status);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order status: orderId={}, status={}", orderId, status);
        dataServiceClient.updateOrderStatus(orderId, status);
    }

    @Override
    public void updateFinalFile(Long orderId, String fileUrl) {
        log.info("Updating final file: orderId={}, fileUrl={}", orderId, fileUrl);
        dataServiceClient.updateFinalFile(orderId, fileUrl);
    }

    @Override
    public void startOrder(Long orderId) {
        log.info("Starting order: orderId={}", orderId);
        dataServiceClient.startOrder(orderId);
    }

    @Override
    public void completeOrder(Long orderId) {
        log.info("Completing order: orderId={}", orderId);
        dataServiceClient.completeOrder(orderId);
    }

    @Override
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: orderId={}", orderId);
        dataServiceClient.cancelOrder(orderId);
    }

    @Override
    public BigDecimal getArtistEarnings(Long artistId) {
        log.info("Getting artist earnings: artistId={}", artistId);
        return dataServiceClient.getArtistEarnings(artistId);
    }

    @Override
    public BigDecimal calculateTotalSpent(Long customerId) {
        List<OrderDto> orders = dataServiceClient.getOrdersByCustomer(customerId);
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .map(OrderDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long getCompletedOrdersCount(Long artistId) {
        List<OrderDto> orders = dataServiceClient.getOrdersByArtist(artistId);
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .count();
    }
}