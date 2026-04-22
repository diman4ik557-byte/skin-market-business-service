package by.step.controller;

import by.step.dto.ApiResponseDto;
import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import by.step.service.OrderBusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с заказами.
 * Является частью бизнес-сервиса и выступает посредником между веб-интерфейсом и дата-сервисом.
 *
 * @author Skin Market Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/business/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderBusinessService orderBusinessService;

    /**
     * Создает новый заказ.
     *
     * @param request DTO с данными для создания заказа (customerId, artistId, description, price)
     * @return ApiResponseDto с созданным заказом
     */
    @PostMapping
    public ResponseEntity<ApiResponseDto<OrderDto>> createOrder(@Valid @RequestBody CreateOrderRequestDto request) {
        log.info("REST запрос: создание заказа от customerId={} художнику artistId={}",
                request.getCustomerId(), request.getArtistId());
        OrderDto order = orderBusinessService.createOrder(request);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ успешно создан", order));
    }

    /**
     * Получает заказ по ID.
     *
     * @param orderId ID заказа
     * @return ApiResponseDto с заказом
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderDto>> getOrder(@PathVariable Long orderId) {
        log.debug("REST запрос: получение заказа по id={}", orderId);
        OrderDto order = orderBusinessService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success(order));
    }

    /**
     * Получает все заказы заказчика.
     *
     * @param customerId ID заказчика
     * @return ApiResponseDto со списком заказов
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrdersByCustomer(@PathVariable Long customerId) {
        log.debug("REST запрос: получение заказов заказчика {}", customerId);
        List<OrderDto> orders = orderBusinessService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }



    /**
     * Получает все заказы художника.
     *
     * @param artistId ID художника
     * @return ApiResponseDto со списком заказов
     */
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrdersByArtist(@PathVariable Long artistId) {
        log.debug("REST запрос: получение заказов художника {}", artistId);
        List<OrderDto> orders = orderBusinessService.getOrdersByArtist(artistId);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }

    /**
     * Получает заказы по статусу.
     *
     * @param status статус заказа (NEW, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED)
     * @return ApiResponseDto со списком заказов
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponseDto<List<OrderDto>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        log.debug("REST запрос: получение заказов по статусу {}", status);
        List<OrderDto> orders = orderBusinessService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }

    /**
     * Обновляет статус заказа.
     *
     * @param orderId ID заказа
     * @param status новый статус
     * @return ApiResponseDto с подтверждением
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseDto<Void>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        log.info("REST запрос: обновление статуса заказа {} на {}", orderId, status);
        orderBusinessService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponseDto.success("Статус заказа обновлён", null));
    }


    /**
     * Начинает выполнение заказа.
     *
     * @param orderId ID заказа
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{orderId}/start")
    public ResponseEntity<ApiResponseDto<Void>> startOrder(@PathVariable Long orderId) {
        log.info("REST запрос: начало выполнения заказа {}", orderId);
        orderBusinessService.startOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ открыт", null));
    }

    /**
     * Отправляет заказ на проверку заказчику.
     *
     * @param orderId ID заказа
     * @param finalFileUrl URL файла для проверки
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{orderId}/submit-review")
    public ResponseEntity<ApiResponseDto<Void>> submitForReview(
            @PathVariable Long orderId,
            @RequestParam String finalFileUrl) {
        log.info("REST запрос: отправка заказа {} на проверку", orderId);
        orderBusinessService.submitForReview(orderId, finalFileUrl);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ отправлен на проверку", null));
    }

    /**
     * Завершает заказ.
     *
     * @param orderId ID заказа
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponseDto<Void>> completeOrder(@PathVariable Long orderId) {
        log.info("REST запрос: завершение заказа {}", orderId);
        orderBusinessService.completeOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ закрыт", null));
    }

    /**
     * Отменяет заказ.
     *
     * @param orderId ID заказа
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponseDto<Void>> cancelOrder(@PathVariable Long orderId) {
        log.info("REST запрос: отмена заказа {}", orderId);
        orderBusinessService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponseDto.success("Заказ отменён", null));
    }

    /**
     * Получает общий заработок художника.
     *
     * @param artistId ID художника
     * @return ApiResponseDto с суммой заработка
     */
    @GetMapping("/artist/{artistId}/earnings")
    public ResponseEntity<ApiResponseDto<BigDecimal>> getArtistEarnings(@PathVariable Long artistId) {
        log.debug("REST запрос: получение заработка художника {}", artistId);
        BigDecimal earnings = orderBusinessService.getArtistEarnings(artistId);
        return ResponseEntity.ok(ApiResponseDto.success(earnings));
    }

    /**
     * Получает общую сумму, потраченную заказчиком.
     *
     * @param customerId ID заказчика
     * @return ApiResponseDto с суммой
     */
    @GetMapping("/customer/{customerId}/total-spent")
    public ResponseEntity<ApiResponseDto<BigDecimal>> getTotalSpent(@PathVariable Long customerId) {
        log.debug("REST запрос: получение общей суммы расходов заказчика {}", customerId);
        BigDecimal total = orderBusinessService.calculateTotalSpent(customerId);
        return ResponseEntity.ok(ApiResponseDto.success(total));
    }

    /**
     * Получает количество завершенных заказов художника.
     *
     * @param artistId ID художника
     * @return ApiResponseDto с количеством
     */
    @GetMapping("/artist/{artistId}/completed-count")
    public ResponseEntity<ApiResponseDto<Long>> getCompletedOrdersCount(@PathVariable Long artistId) {
        log.debug("REST запрос: получение количества завершенных заказов художника {}", artistId);
        long count = orderBusinessService.getCompletedOrdersCount(artistId);
        return ResponseEntity.ok(ApiResponseDto.success(count));
    }

    /**
     * Получает заказы заказчика с пагинацией.
     *
     * @param customerId ID заказчика
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей заказов
     */
    @GetMapping("/customer/{customerId}/page")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getOrdersByCustomerPage(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.debug("REST запрос: получение заказов заказчика {} с пагинацией: page={}, size={}", customerId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<OrderDto> orders = orderBusinessService.getOrdersByCustomerPage(customerId, pageable);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }

    /**
     * Получает заказы художника с пагинацией.
     *
     * @param artistId ID художника
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей заказов
     */
    @GetMapping("/artist/{artistId}/page")
    public ResponseEntity<ApiResponseDto<Page<OrderDto>>> getOrdersByArtistPage(
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.debug("REST запрос: получение заказов художника {} с пагинацией: page={}, size={}", artistId, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<OrderDto> orders = orderBusinessService.getOrdersByArtistPage(artistId, pageable);
        return ResponseEntity.ok(ApiResponseDto.success(orders));
    }


}