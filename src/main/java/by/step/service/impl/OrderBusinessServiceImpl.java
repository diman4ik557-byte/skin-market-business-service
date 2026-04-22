package by.step.service.impl;

import by.step.client.DataServiceClient;
import by.step.dto.CreateOrderRequestDto;
import by.step.dto.OrderDto;
import by.step.enums.OrderStatus;
import by.step.service.OrderBusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация бизнес-сервиса для работы с заказами.
 * Выступает посредником между веб-слоем и data-service,
 * добавляя кэширование и бизнес-логику.
 *
 * @author Skin Market Team
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderBusinessServiceImpl implements OrderBusinessService {

    private final DataServiceClient dataServiceClient;

    /**
     * Создаёт новый заказ через data-service.
     * Очищает кэши заказов после создания.
     *
     * @param request DTO с данными для создания заказа
     * @return созданный заказ
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"ordersByCustomer", "ordersByArtist", "ordersByStatus"}, allEntries = true)
    public OrderDto createOrder(CreateOrderRequestDto request) {
        log.info("Creating order: customerId={}, artistId={}, price={}",
                request.getCustomerId(), request.getArtistId(), request.getPrice());

        try {
            OrderDto result = dataServiceClient.createOrder(
                    request.getCustomerId(),
                    request.getArtistId(),
                    request.getDescription(),
                    request.getPrice()
            );
            log.info("Order created successfully: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
    }

    /**
     * Получает заказ по ID с использованием кэша.
     *
     * @param orderId идентификатор заказа
     * @return заказ
     * @throws RuntimeException если заказ не найден
     */
    @Override
    @Cacheable(value = "orderById", key = "#orderId")
    public OrderDto getOrder(Long orderId) {
        log.info("Getting order from data-service: orderId={}", orderId);
        try {
            OrderDto result = dataServiceClient.getOrderById(orderId);
            log.info("Order found: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error getting order {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Failed to get order: " + orderId, e);
        }
    }

    /**
     * Получает все заказы заказчика.
     * Результат кэшируется.
     *
     * @param customerId идентификатор заказчика
     * @return список заказов
     */
    @Override
    @Cacheable(value = "ordersByCustomer", key = "#customerId")
    public List<OrderDto> getOrdersByCustomer(Long customerId) {
        log.info("Getting orders by customer from data-service: customerId={}", customerId);
        try {
            Page<OrderDto> page = dataServiceClient.getOrdersByCustomer(customerId, Pageable.unpaged());
            if (page != null && page.getContent() != null) {
                return page.getContent();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting orders by customer {}: {}", customerId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Получает все заказы художника.
     * Результат кэшируется для уменьшения нагрузки на data-service.
     *
     * @param artistId идентификатор художника
     * @return список заказов художника (может быть пустым)
     */
    @Override
    @Cacheable(value = "ordersByArtist", key = "#artistId")
    public List<OrderDto> getOrdersByArtist(Long artistId) {
        log.info("Getting orders by artist from data-service: artistId={}", artistId);
        try {
            Page<OrderDto> page = dataServiceClient.getOrdersByArtist(artistId, Pageable.unpaged());
            if (page != null && page.getContent() != null) {
                return page.getContent();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting orders by artist {}: {}", artistId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Получает заказы по статусу.
     * Результат кэшируется для уменьшения нагрузки на data-service.
     *
     * @param status статус заказа (NEW, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED)
     * @return список заказов с указанным статусом (может быть пустым)
     */
    @Override
    @Cacheable(value = "ordersByStatus", key = "#status")
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        log.info("Getting orders by status from data-service: status={}", status);
        try {
            Page<OrderDto> page = dataServiceClient.getOrdersByStatus(status, Pageable.unpaged());
            if (page != null && page.getContent() != null) {
                return page.getContent();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting orders by status {}: {}", status, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Обновляет статус заказа.
     * После обновления очищает кэш этого заказа.
     *
     * @param orderId идентификатор заказа
     * @param status новый статус
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"orderById", "ordersByCustomer", "ordersByArtist", "ordersByStatus"}, key = "#orderId")
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order status: orderId={}, status={}", orderId, status);
        try {
            dataServiceClient.updateOrderStatus(orderId, status);
            log.info("Order status updated successfully");
        } catch (Exception e) {
            log.error("Error updating order status: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    /**
     * Обновляет URL финального файла заказа.
     * После обновления очищает кэш этого заказа.
     *
     * @param orderId идентификатор заказа
     * @param fileUrl URL готового скина
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"orderById", "ordersByCustomer", "ordersByArtist"}, key = "#orderId")
    public void updateFinalFile(Long orderId, String fileUrl) {
        log.info("Updating final file: orderId={}, fileUrl={}", orderId, fileUrl);
        try {
            dataServiceClient.updateFinalFile(orderId, fileUrl);
            log.info("Final file updated successfully");
        } catch (Exception e) {
            log.error("Error updating final file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update final file", e);
        }
    }

    /**
     * Начинает выполнение заказа.
     * Меняет статус с NEW на IN_PROGRESS.
     * После изменения очищает кэш этого заказа.
     *
     * @param orderId идентификатор заказа
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"orderById", "ordersByCustomer", "ordersByArtist"}, key = "#orderId")
    public void startOrder(Long orderId) {
        log.info("Starting order: orderId={}", orderId);
        try {
            dataServiceClient.startOrder(orderId);
            log.info("Order started successfully");
        } catch (Exception e) {
            log.error("Error starting order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start order: " + e.getMessage(), e);
        }
    }

    /**
     * Завершает заказ.
     * Меняет статус с REVIEW на COMPLETED и переводит оплату художнику.
     * После изменения очищает кэш этого заказа.
     *
     * @param orderId идентификатор заказа
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"orderById", "ordersByCustomer", "ordersByArtist"}, key = "#orderId")
    public void completeOrder(Long orderId) {
        log.info("Completing order: orderId={}", orderId);
        try {
            dataServiceClient.completeOrder(orderId);
            log.info("Order completed successfully");
        } catch (Exception e) {
            log.error("Error completing order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to complete order: " + e.getMessage(), e);
        }
    }

    /**
     * Отменяет заказ.
     * Возвращает средства заказчику, если заказ не был завершён.
     * После отмены очищает кэш этого заказа.
     *
     * @param orderId идентификатор заказа
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"orderById", "ordersByCustomer", "ordersByArtist"}, key = "#orderId")
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order: orderId={}", orderId);
        try {
            dataServiceClient.cancelOrder(orderId);
            log.info("Order cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to cancel order: " + e.getMessage(), e);
        }
    }

    /**
     * Отправляет заказ на проверку заказчику.
     * Меняет статус с IN_PROGRESS на REVIEW.
     * После отправки очищает кэш этого заказа.
     *
     * @param orderId идентификатор заказа
     * @param finalFileUrl URL файла для проверки
     * @throws RuntimeException если data-service вернул ошибку
     */
    @Override
    @CacheEvict(value = {"orderById", "ordersByCustomer", "ordersByArtist"}, key = "#orderId")
    public void submitForReview(Long orderId, String finalFileUrl) {
        log.info("Submitting order for review: orderId={}, finalFileUrl={}", orderId, finalFileUrl);
        try {
            dataServiceClient.submitForReview(orderId, finalFileUrl);
            log.info("Order submitted for review successfully");
        } catch (Exception e) {
            log.error("Error submitting order for review: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to submit order for review: " + e.getMessage(), e);
        }
    }

    /**
     * Получает общий заработок художника по завершённым заказам.
     * Результат кэшируется.
     *
     * @param artistId идентификатор художника
     * @return сумма заработка (0 если ошибка или нет заказов)
     */
    @Override
    @Cacheable(value = "artistEarnings", key = "#artistId")
    public BigDecimal getArtistEarnings(Long artistId) {
        log.info("Getting artist earnings from data-service: artistId={}", artistId);
        try {
            return dataServiceClient.getArtistEarnings(artistId);
        } catch (Exception e) {
            log.error("Error getting artist earnings: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Рассчитывает общую сумму, потраченную заказчиком на завершённые заказы.
     *
     * @param customerId идентификатор заказчика
     * @return общая сумма расходов
     */
    @Override
    public BigDecimal calculateTotalSpent(Long customerId) {
        log.info("Calculating total spent: customerId={}", customerId);
        try {
            List<OrderDto> orders = getOrdersByCustomer(customerId);
            return orders.stream()
                    .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                    .map(OrderDto::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            log.error("Error calculating total spent: {}", e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Получает количество завершённых заказов художника.
     *
     * @param artistId идентификатор художника
     * @return количество завершённых заказов
     */
    @Override
    public long getCompletedOrdersCount(Long artistId) {
        log.info("Getting completed orders count: artistId={}", artistId);
        try {
            List<OrderDto> orders = getOrdersByArtist(artistId);
            return orders.stream()
                    .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                    .count();
        } catch (Exception e) {
            log.error("Error getting completed orders count: {}", e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * Получает все заказы заказчика.
     *
     * @param pageable параметры пагинации
     * @return страница со всеми заказами
     */
    @Override
    public Page<OrderDto> getOrdersByCustomerPage(Long customerId, Pageable pageable) {
        log.info("Getting orders by customer with pagination: customerId={}", customerId);
        return dataServiceClient.getOrdersByCustomer(customerId, pageable);
    }

    /**
     * Получает все заказы художника.
     *
     * @param pageable параметры пагинации
     * @return страницу заказов художника
     */
    @Override
    public Page<OrderDto> getOrdersByArtistPage(Long artistId, Pageable pageable) {
        log.info("Getting orders by artist with pagination: artistId={}", artistId);
        return dataServiceClient.getOrdersByArtist(artistId, pageable);
    }
}