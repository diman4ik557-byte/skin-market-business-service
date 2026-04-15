package by.step.controller;

import by.step.dto.ApiResponseDto;
import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import by.step.service.OrderBusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/business/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderBusinessService orderBusinessService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequestDto request) {
        OrderDto order = orderBusinessService.createOrder(request);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ успешно создан", order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderDto>> getOrder(@PathVariable Long orderId) {
        OrderDto order = orderBusinessService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success(order));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<OrderDto> orders = orderBusinessService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrdersByArtist(@PathVariable Long artistId) {
        List<OrderDto> orders = orderBusinessService.getOrdersByArtist(artistId);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderDto> orders = orderBusinessService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseDto<Void>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        orderBusinessService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponseDto.success("Статус заказа обновлён", null));
    }

    @PutMapping("/{orderId}/final-file")
    public ResponseEntity<ApiResponseDto<Void>> updateFinalFile(
            @PathVariable Long orderId,
            @RequestParam String fileUrl) {
        orderBusinessService.updateFinalFile(orderId, fileUrl);
        return ResponseEntity.ok(ApiResponseDto.success("Финальный файл удалён", null));
    }

    @PostMapping("/{orderId}/start")
    public ResponseEntity<ApiResponseDto<Void>> startOrder(@PathVariable Long orderId) {
        orderBusinessService.startOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ открыт", null));
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponseDto<Void>> completeOrder(@PathVariable Long orderId) {
        orderBusinessService.completeOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ закрыт", null));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponseDto<Void>> cancelOrder(@PathVariable Long orderId) {
        orderBusinessService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ отменён", null));
    }

    @GetMapping("/artist/{artistId}/earnings")
    public ResponseEntity<ApiResponseDto<BigDecimal>> getArtistEarnings(@PathVariable Long artistId) {
        BigDecimal earnings = orderBusinessService.getArtistEarnings(artistId);
        return ResponseEntity.ok(ApiResponseDto.success(earnings));
    }

    @GetMapping("/customer/{customerId}/total-spent")
    public ResponseEntity<ApiResponseDto<BigDecimal>> getTotalSpent(@PathVariable Long customerId) {
        BigDecimal total = orderBusinessService.calculateTotalSpent(customerId);
        return ResponseEntity.ok(ApiResponseDto.success(total));
    }

    @GetMapping("/artist/{artistId}/completed-count")
    public ResponseEntity<ApiResponseDto<Long>> getCompletedOrdersCount(@PathVariable Long artistId) {
        long count = orderBusinessService.getCompletedOrdersCount(artistId);
        return ResponseEntity.ok(ApiResponseDto.success(count));
    }
}