package by.step.controller;

import by.step.client.DataServiceClient;
import by.step.dto.ApiResponseDto;
import by.step.dto.ArtistProfileDto;
import by.step.dto.StudioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных со студиями художников.
 * Является частью бизнес-сервиса и выступает посредником между веб-интерфейсом и дата-сервисом.
 *
 * @author Skin Market Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/business/studios")
@RequiredArgsConstructor
public class StudioController {

    private final DataServiceClient dataServiceClient;

    /**
     * Создает новую студию.
     *
     * @param userId ID пользователя-создателя (должен быть художником)
     * @param name название студии
     * @param description описание студии
     * @return ApiResponseDto с созданной студией
     */
    @PostMapping
    public ApiResponseDto<StudioDto> createStudio(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestParam String description) {
        log.info("REST запрос: создание студии userId={}, name={}", userId, name);
        StudioDto studio = dataServiceClient.createStudio(userId, name, description);
        return ApiResponseDto.success(studio);
    }

    /**
     * Получает студию по ID.
     *
     * @param studioId ID студии
     * @return ApiResponseDto со студией
     */
    @GetMapping("/{studioId}")
    public ApiResponseDto<StudioDto> getStudioById(@PathVariable Long studioId) {
        log.debug("REST запрос: получение студии по id={}", studioId);
        StudioDto studio = dataServiceClient.getStudioById(studioId);
        return ApiResponseDto.success(studio);
    }

    /**
     * Получает список всех студий.
     *
     * @return ApiResponseDto со списком студий
     */
    @GetMapping
    public ApiResponseDto<List<StudioDto>> getAllStudios() {
        log.debug("REST запрос: получение всех студий");
        List<StudioDto> studios = dataServiceClient.getAllStudios();
        return ApiResponseDto.success(studios);
    }

    /**
     * Получает студию по ID пользователя (владельца).
     *
     * @param userId ID пользователя
     * @return ApiResponseDto со студией
     */
    @GetMapping("/user/{userId}")
    public ApiResponseDto<StudioDto> getStudioByUserId(@PathVariable Long userId) {
        log.debug("REST запрос: получение студии по userId={}", userId);
        StudioDto studio = dataServiceClient.getStudioByUserId(userId);
        return ApiResponseDto.success(studio);
    }

    /**
     * Получает список участников студии.
     *
     * @param studioId ID студии
     * @return ApiResponseDto со списком участников
     */
    @GetMapping("/{studioId}/members")
    public ApiResponseDto<List<ArtistProfileDto>> getStudioMembers(@PathVariable Long studioId) {
        log.debug("REST запрос: получение участников студии {}", studioId);
        List<ArtistProfileDto> members = dataServiceClient.getStudioMembers(studioId);
        return ApiResponseDto.success(members);
    }

    /**
     * Подает заявку на вступление в студию.
     *
     * @param studioId ID студии
     * @param artistId ID художника
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{studioId}/members/{artistId}/request")
    public ApiResponseDto<Void> requestToJoinStudio(
            @PathVariable Long studioId,
            @PathVariable Long artistId) {
        log.info("REST запрос: заявка художника {} на вступление в студию {}", artistId, studioId);
        dataServiceClient.requestToJoinStudio(studioId, artistId);
        return ApiResponseDto.success(null);
    }

    /**
     * Одобряет заявку на вступление в студию.
     *
     * @param studioId ID студии
     * @param artistId ID художника
     * @param managerId ID менеджера (для проверки прав)
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{studioId}/members/{artistId}/approve")
    public ApiResponseDto<Void> approveStudioMember(
            @PathVariable Long studioId,
            @PathVariable Long artistId,
            @RequestParam Long managerId) {
        log.info("REST запрос: одобрение заявки художника {} в студию {} менеджером {}", artistId, studioId, managerId);
        dataServiceClient.approveStudioMember(studioId, artistId, managerId);
        return ApiResponseDto.success(null);
    }

    /**
     * Выход участника из студии по собственному желанию.
     *
     * @param studioId ID студии
     * @param artistId ID художника
     * @return ApiResponseDto с подтверждением
     */
    @PostMapping("/{studioId}/members/{artistId}/leave")
    public ApiResponseDto<Void> leaveStudio(
            @PathVariable Long studioId,
            @PathVariable Long artistId) {
        log.info("REST запрос: выход художника {} из студии {}", artistId, studioId);
        dataServiceClient.leaveStudio(studioId, artistId);
        return ApiResponseDto.success(null);
    }

    /**
     * Удаляет участника из студии (только для менеджера).
     *
     * @param studioId ID студии
     * @param artistId ID художника
     * @param managerId ID менеджера (для проверки прав)
     * @return ApiResponseDto с подтверждением
     */
    @DeleteMapping("/{studioId}/members/{artistId}")
    public ApiResponseDto<Void> removeStudioMember(
            @PathVariable Long studioId,
            @PathVariable Long artistId,
            @RequestParam Long managerId) {
        log.info("REST запрос: удаление художника {} из студии {} менеджером {}", artistId, studioId, managerId);
        dataServiceClient.removeStudioMember(studioId, artistId, managerId);
        return ApiResponseDto.success(null);
    }

    /**
     * Обновляет описание студии (только для менеджера).
     *
     * @param studioId ID студии
     * @param description новое описание
     * @param managerId ID менеджера (для проверки прав)
     * @return ApiResponseDto с обновленной студией
     */
    @PutMapping("/{studioId}")
    public ApiResponseDto<StudioDto> updateStudio(
            @PathVariable Long studioId,
            @RequestParam String description,
            @RequestParam Long managerId) {
        log.info("REST запрос: обновление описания студии {} менеджером {}", studioId, managerId);
        StudioDto studio = dataServiceClient.updateStudio(studioId, description, managerId);
        return ApiResponseDto.success(studio);
    }

    /**
     * Удаляет студию (только для менеджера).
     *
     * @param studioId ID студии
     * @param managerId ID менеджера (для проверки прав)
     * @return ApiResponseDto с подтверждением
     */
    @DeleteMapping("/{studioId}")
    public ApiResponseDto<Void> deleteStudio(
            @PathVariable Long studioId,
            @RequestParam Long managerId) {
        log.info("REST запрос: удаление студии {} менеджером {}", studioId, managerId);
        dataServiceClient.deleteStudio(studioId, managerId);
        return ApiResponseDto.success(null);
    }

    /**
     * Проверяет, является ли пользователь менеджером студии.
     *
     * @param studioId ID студии
     * @param userId ID пользователя
     * @return true если пользователь менеджер или администратор
     */
    @GetMapping("/{studioId}/is-manager")
    public boolean isManager(
            @PathVariable Long studioId,
            @RequestParam Long userId) {
        log.debug("REST запрос: проверка прав менеджера для пользователя {} в студии {}", userId, studioId);
        return dataServiceClient.isManager(studioId, userId);
    }

    /**
     * Получает список всех студий с пагинацией.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @param sort поле для сортировки
     * @param direction направление сортировки (ASC/DESC)
     * @return ApiResponseDto со страницей студий
     */
    @GetMapping("/page")
    public ApiResponseDto<Page<StudioDto>> getAllStudiosPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        Page<StudioDto> studios = dataServiceClient.getAllStudiosPage(pageable);
        return ApiResponseDto.success(studios);
    }

}