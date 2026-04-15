package by.step.client;

import by.step.dto.OrderDto;
import by.step.dto.UserDto;
import by.step.enums.OrderStatus;
import by.step.enums.UserRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "data-service", url = "${data-service.url:http://localhost:8081}")
public interface DataServiceClient {

    // User endpoints
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/users/username/{username}")
    UserDto getUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/users")
    List<UserDto> getAllUsers();

    @GetMapping("/api/users/role/{role}")
    List<UserDto> getUsersByRole(@PathVariable("role") UserRole role);

    @PostMapping("/api/users/{id}/balance/add")
    void addToBalance(@PathVariable("id") Long id, @RequestParam BigDecimal amount);

    // Order endpoints
    @PostMapping("/api/orders")
    OrderDto createOrder(@RequestParam Long customerId,
                         @RequestParam Long artistId,
                         @RequestParam String description,
                         @RequestParam BigDecimal price);

    @GetMapping("/api/orders/{orderId}")
    OrderDto getOrderById(@PathVariable("orderId") Long orderId);

    @GetMapping("/api/orders/customer/{customerId}")
    List<OrderDto> getOrdersByCustomer(@PathVariable("customerId") Long customerId);

    @GetMapping("/api/orders/artist/{artistId}")
    List<OrderDto> getOrdersByArtist(@PathVariable("artistId") Long artistId);

    @GetMapping("/api/orders/status/{status}")
    List<OrderDto> getOrdersByStatus(@PathVariable("status") OrderStatus status);

    @PutMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") Long orderId,
                           @RequestParam OrderStatus status);

    @PutMapping("/api/orders/{orderId}/final-file")
    void updateFinalFile(@PathVariable("orderId") Long orderId,
                         @RequestParam String fileUrl);

    @PostMapping("/api/orders/{orderId}/start")
    void startOrder(@PathVariable("orderId") Long orderId);

    @PostMapping("/api/orders/{orderId}/complete")
    void completeOrder(@PathVariable("orderId") Long orderId);

    @PostMapping("/api/orders/{orderId}/cancel")
    void cancelOrder(@PathVariable("orderId") Long orderId);

    @GetMapping("/api/orders/artist/{artistId}/earnings")
    BigDecimal getArtistEarnings(@PathVariable("artistId") Long artistId);
}
