package by.step.client;

import by.step.dto.ArtistProfileDto;
import by.step.dto.StudioDto;
import java.util.List;
import by.step.dto.*;
import by.step.enums.OrderStatus;
import by.step.enums.UserRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PostMapping("/api/users/register")
    UserDto registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam UserRole role
    );

    // Balance endpoints
    @PostMapping("/api/users/{id}/balance/add")
    void addToBalance(@PathVariable("id") Long id, @RequestParam BigDecimal amount);

    @PostMapping("/api/users/{id}/balance/subtract")
    void subtractFromBalance(@PathVariable("id") Long id, @RequestParam BigDecimal amount);

    @PutMapping("/api/users/{id}/balance")
    void updateBalance(@PathVariable("id") Long id, @RequestParam BigDecimal balance);

    @GetMapping("/api/users/{id}/balance/sufficient")
    boolean hasEnoughBalance(@PathVariable("id") Long id, @RequestParam BigDecimal amount);

    // Order endpoints
    @PostMapping("/api/orders")
    OrderDto createOrder(@RequestParam Long customerId,
                         @RequestParam Long artistId,
                         @RequestParam String description,
                         @RequestParam BigDecimal price);

    @GetMapping("/api/orders/{orderId}")
    OrderDto getOrderById(@PathVariable("orderId") Long orderId);

    @GetMapping("/api/orders/customer/{customerId}")
    Page<OrderDto> getOrdersByCustomer(@PathVariable("customerId") Long customerId, Pageable pageable);

    @GetMapping("/api/orders/artist/{artistId}")
    Page<OrderDto> getOrdersByArtist(@PathVariable("artistId") Long artistId, Pageable pageable);

    @GetMapping("/api/orders/status/{status}")
    Page<OrderDto> getOrdersByStatus(@PathVariable("status") OrderStatus status, Pageable pageable);

    @PostMapping("/api/orders/{orderId}/submit-review")
    void submitForReview(@PathVariable("orderId") Long orderId, @RequestParam String finalFileUrl);

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

    @GetMapping("/api/users/role/{role}/page")
    Page<UserDto> getUsersByRolePage(@PathVariable("role") UserRole role, Pageable pageable);

    @GetMapping("/api/users/page")
    Page<UserDto> getAllUsersPage(Pageable pageable);

    // Studio endpoints
    @PostMapping("/api/studios")
    StudioDto createStudio(@RequestParam Long userId,
                           @RequestParam String name,
                           @RequestParam String description);

    @GetMapping("/api/studios/{studioId}")
    StudioDto getStudioById(@PathVariable("studioId") Long studioId);

    @GetMapping("/api/studios")
    List<StudioDto> getAllStudios();

    @GetMapping("/api/studios/user/{userId}")
    StudioDto getStudioByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/api/studios/{studioId}/members")
    List<ArtistProfileDto> getStudioMembers(@PathVariable("studioId") Long studioId);

    @PostMapping("/api/studios/{studioId}/members/{artistId}/request")
    void requestToJoinStudio(@PathVariable("studioId") Long studioId,
                             @PathVariable("artistId") Long artistId);

    @PostMapping("/api/studios/{studioId}/members/{artistId}/approve")
    void approveStudioMember(@PathVariable("studioId") Long studioId,
                             @PathVariable("artistId") Long artistId,
                             @RequestParam Long managerId);

    @DeleteMapping("/api/studios/{studioId}/members/{artistId}")
    void removeStudioMember(@PathVariable("studioId") Long studioId,
                            @PathVariable("artistId") Long artistId,
                            @RequestParam Long managerId);

    @PutMapping("/api/studios/{studioId}")
    StudioDto updateStudio(@PathVariable("studioId") Long studioId,
                           @RequestParam String description,
                           @RequestParam Long managerId);

    @DeleteMapping("/api/studios/{studioId}")
    void deleteStudio(@PathVariable("studioId") Long studioId,
                      @RequestParam Long managerId);

    @PostMapping("/api/studios/{studioId}/members/{artistId}/leave")
    void leaveStudio(@PathVariable("studioId") Long studioId,
                     @PathVariable("artistId") Long artistId);

    @GetMapping("/api/studios/{studioId}/is-manager")
    boolean isManager(@PathVariable("studioId") Long studioId,
                      @RequestParam Long userId);

    @GetMapping("/api/studios/page")
    Page<StudioDto> getAllStudiosPage(Pageable pageable);

    // Message endpoints for orders
    @GetMapping("/api/messages/order/{orderId}")
    Page<MessageDto> getMessagesByOrder(@PathVariable("orderId") Long orderId, Pageable pageable);

    @PostMapping("/api/messages/order/{orderId}/send")
    MessageDto sendMessage(@PathVariable("orderId") Long orderId,
                           @RequestParam Long senderId,
                           @RequestParam String content);

    @PostMapping("/api/messages/order/{orderId}/preview")
    MessageDto sendPreview(@PathVariable("orderId") Long orderId,
                           @RequestParam Long senderId,
                           @RequestParam String content,
                           @RequestParam String attachmentUrl);

    @PostMapping("/api/messages/order/{orderId}/attachment")
    MessageDto sendAttachment(@PathVariable("orderId") Long orderId,
                              @RequestParam Long senderId,
                              @RequestParam String attachmentUrl);

    // Message endpoints for studios
    @GetMapping("/api/messages/studio/{studioId}")
    Page<MessageDto> getStudioMessages(@PathVariable("studioId") Long studioId, Pageable pageable);

    @PostMapping("/api/messages/studio/{studioId}/send")
    MessageDto sendToStudio(@PathVariable("studioId") Long studioId,
                            @RequestParam Long senderId,
                            @RequestParam String content,
                            @RequestParam(required = false) String attachmentUrl);

    @PostMapping("/api/messages/studio/{studioId}/send-to-artist")
    MessageDto sendToArtist(@PathVariable("studioId") Long studioId,
                            @RequestParam Long senderId,
                            @RequestParam Long receiverId,
                            @RequestParam String content,
                            @RequestParam(required = false) String attachmentUrl);

    @GetMapping("/api/messages/studio/{studioId}/unassigned")
    List<MessageDto> getUnassignedStudioMessages(@PathVariable("studioId") Long studioId);

    @PostMapping("/api/messages/{messageId}/redirect")
    MessageDto redirectToArtist(@PathVariable("messageId") Long messageId,
                                @RequestParam Long receiverId,
                                @RequestParam Long managerId);

    // User role update
    @PutMapping("/api/users/{id}/role")
    UserDto updateUserRole(@PathVariable("id") Long id, @RequestParam String role);
}