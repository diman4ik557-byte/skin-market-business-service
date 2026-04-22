package by.step.controller;

import by.step.client.DataServiceClient;
import by.step.dto.ApiResponseDto;
import by.step.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с сообщениями.
 * Является частью бизнес-сервиса и выступает посредником между веб-интерфейсом и дата-сервисом.
 *
 * @author Skin Market Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/business/messages")
@RequiredArgsConstructor
public class MessageController {

    private final DataServiceClient dataServiceClient;

    // ==================== СООБЩЕНИЯ В ЗАКАЗАХ ====================

    /**
     * Получает сообщения по заказу с пагинацией.
     *
     * @param orderId ID заказа
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей сообщений
     */
    @GetMapping("/order/{orderId}")
    public ApiResponseDto<Page<MessageDto>> getMessagesByOrder(
            @PathVariable Long orderId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort,
            @RequestParam String direction) {

        log.debug("REST запрос: получение сообщений по заказу {}", orderId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<MessageDto> messages = dataServiceClient.getMessagesByOrder(orderId, pageable);
        return ApiResponseDto.success(messages);
    }

    /**
     * Отправляет сообщение в чат заказа.
     *
     * @param orderId ID заказа
     * @param senderId ID отправителя
     * @param content текст сообщения
     * @return ApiResponseDto с отправленным сообщением
     */
    @PostMapping("/order/{orderId}/send")
    public ApiResponseDto<MessageDto> sendMessage(
            @PathVariable Long orderId,
            @RequestParam Long senderId,
            @RequestParam String content) {
        log.info("REST запрос: отправка сообщения в заказ {} от пользователя {}", orderId, senderId);
        MessageDto message = dataServiceClient.sendMessage(orderId, senderId, content);
        return ApiResponseDto.success(message);
    }

    /**
     * Отправляет предпросмотр работы в чат заказа.
     *
     * @param orderId ID заказа
     * @param senderId ID отправителя
     * @param content описание предпросмотра
     * @param attachmentUrl URL изображения
     * @return ApiResponseDto с отправленным предпросмотром
     */
    @PostMapping("/order/{orderId}/preview")
    public ApiResponseDto<MessageDto> sendPreview(
            @PathVariable Long orderId,
            @RequestParam Long senderId,
            @RequestParam String content,
            @RequestParam String attachmentUrl) {
        log.info("REST запрос: отправка предпросмотра в заказ {} от пользователя {}", orderId, senderId);
        MessageDto message = dataServiceClient.sendPreview(orderId, senderId, content, attachmentUrl);
        return ApiResponseDto.success(message);
    }

    /**
     * Отправляет файл в чат заказа.
     *
     * @param orderId ID заказа
     * @param senderId ID отправителя
     * @param attachmentUrl URL файла
     * @return ApiResponseDto с отправленным файлом
     */
    @PostMapping("/order/{orderId}/attachment")
    public ApiResponseDto<MessageDto> sendAttachment(
            @PathVariable Long orderId,
            @RequestParam Long senderId,
            @RequestParam String attachmentUrl) {
        log.info("REST запрос: отправка файла в заказ {} от пользователя {}", orderId, senderId);
        MessageDto message = dataServiceClient.sendAttachment(orderId, senderId, attachmentUrl);
        return ApiResponseDto.success(message);
    }

    // ==================== СООБЩЕНИЯ В СТУДИЯХ ====================

    /**
     * Получает сообщения по студии с пагинацией.
     *
     * @param studioId ID студии
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей сообщений
     */
    @GetMapping("/studio/{studioId}")
    public ApiResponseDto<Page<MessageDto>> getStudioMessages(
            @PathVariable Long studioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "sentAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        log.debug("REST запрос: получение сообщений по студии {}", studioId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<MessageDto> messages = dataServiceClient.getStudioMessages(studioId, pageable);
        return ApiResponseDto.success(messages);
    }
    /**
     * Отправляет сообщение в чат студии.
     *
     * @param studioId ID студии
     * @param senderId ID отправителя
     * @param content текст сообщения
     * @param attachmentUrl URL файла (опционально)
     * @return ApiResponseDto с отправленным сообщением
     */
    @PostMapping("/studio/{studioId}/send")
    public ApiResponseDto<MessageDto> sendToStudio(
            @PathVariable Long studioId,
            @RequestParam Long senderId,
            @RequestParam String content,
            @RequestParam(required = false) String attachmentUrl) {
        log.info("REST запрос: отправка сообщения в студию {} от пользователя {}", studioId, senderId);
        MessageDto message = dataServiceClient.sendToStudio(studioId, senderId, content, attachmentUrl);
        return ApiResponseDto.success(message);
    }

    /**
     * Отправляет сообщение конкретному художнику от имени менеджера.
     *
     * @param studioId ID студии
     * @param senderId ID отправителя (менеджера)
     * @param receiverId ID получателя (художника)
     * @param content текст сообщения
     * @param attachmentUrl URL файла (опционально)
     * @return ApiResponseDto с отправленным сообщением
     */
    @PostMapping("/studio/{studioId}/send-to-artist")
    public ApiResponseDto<MessageDto> sendToArtist(
            @PathVariable Long studioId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(required = false) String attachmentUrl) {
        log.info("REST запрос: перенаправление сообщения в студии {} от {} к {}", studioId, senderId, receiverId);
        MessageDto message = dataServiceClient.sendToArtist(studioId, senderId, receiverId, content, attachmentUrl);
        return ApiResponseDto.success(message);
    }

    /**
     * Получает список неперенаправленных сообщений студии.
     *
     * @param studioId ID студии
     * @return ApiResponseDto со списком сообщений
     */
    @GetMapping("/studio/{studioId}/unassigned")
    public ApiResponseDto<List<MessageDto>> getUnassignedStudioMessages(@PathVariable Long studioId) {
        log.debug("REST запрос: получение неперенаправленных сообщений студии {}", studioId);
        List<MessageDto> messages = dataServiceClient.getUnassignedStudioMessages(studioId);
        return ApiResponseDto.success(messages);
    }
}