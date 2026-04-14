package by.step.client;

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

    @PutMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") Long orderId,
                           @RequestParam String status);
}
