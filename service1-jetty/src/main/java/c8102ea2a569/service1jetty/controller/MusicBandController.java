package c8102ea2a569.service1jetty.controller;

import c8102ea2a569.service1jetty.dto.*;
import c8102ea2a569.service1jetty.exception.BadRequestException;
import c8102ea2a569.service1jetty.exception.ResourceNotFoundException;
import c8102ea2a569.service1jetty.exception.ValidationException;
import c8102ea2a569.service1jetty.service.MusicBandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
@Tag(name = "Music Band")
@RequiredArgsConstructor
public class MusicBandController {

    private final MusicBandService musicBandService;

    @PostMapping
    @Operation(summary = "Создать новую музыкальную группу",
            description = "Добавляет новую музыкальную группу в коллекцию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Группа успешно создана",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MusicBandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Нарушение ограничений целостности данных",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MusicBandResponse> createMusicBand(@Valid @RequestBody MusicBandDTO dto) {
        MusicBandResponse created = musicBandService.createBand(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить группу по ID",
            description = "Возвращает музыкальную группу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Группа найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MusicBandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Группа не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MusicBandResponse> getMusicBandById(@PathVariable Integer id) {
        if (id <= 0) throw new ValidationException("ID должен быть больше 0");
        return musicBandService.getBandById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с ID " + id + " не найдена"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить группу",
            description = "Обновляет данные существующей музыкальной группы по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Группа успешно обновлена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MusicBandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Группа не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Нарушение ограничений целостности данных",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MusicBandResponse> updateMusicBand(@PathVariable Integer id,
                                                             @Valid @RequestBody MusicBandDTO dto) {
        if (id <= 0) throw new ValidationException("ID должен быть больше 0");
        MusicBandResponse updated = musicBandService.updateBand(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с ID " + id + " не найдена"));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить группу",
            description = "Удаляет музыкальную группу из коллекции по её ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Группа успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Группа не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteMusicBand(@PathVariable Integer id) {
        if (id <= 0) throw new ValidationException("ID должен быть больше 0");
        musicBandService.getBandById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с ID " + id + " не найдена"));
        musicBandService.deleteBand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Получить список групп",
            description = "Возвращает массив музыкальных групп с поддержкой сортировки, фильтрации и постраничного вывода")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список групп успешно получен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MusicBandResponse.class)))
    })
    public ResponseEntity<List<MusicBandResponse>> getAllMusicBands(
            @Parameter(description = "Поле для сортировки (например: name, albumsCount, creationDate)")
            @RequestParam(required = false) String sortBy,

            @Parameter(description = "Фильтр по любому полю (формат: field=value)")
            @RequestParam(required = false) String filterBy,

            @Parameter(description = "Номер страницы")
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Размер страницы")
            @RequestParam(required = false, defaultValue = "10") Integer size,

            HttpServletResponse response) {

        if (page < 0 || size <= 0) {
            throw new BadRequestException("Некорректные параметры пагинации: page >= 0, size > 0");
        }

        List<MusicBandResponse> bands = musicBandService.getAllBands(sortBy, filterBy, page, size);

        // Добавьте эти строки для передачи информации о пагинации:
        long totalElements = musicBandService.getTotalCount(filterBy);
        response.setHeader("X-Total-Count", String.valueOf(totalElements));

        return ResponseEntity.ok(bands);
    }

    @GetMapping("/albums/average")
    @Operation(summary = "Рассчитать среднее количество альбомов",
            description = "Возвращает среднее значение поля albumsCount для всех музыкальных групп в коллекции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Среднее значение успешно рассчитано",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumsAverageResponse.class)))
    })
    public ResponseEntity<AlbumsAverageResponse> getAverageAlbumsCount() {
        double average = musicBandService.getAverageAlbumsCount();
        return ResponseEntity.ok(new AlbumsAverageResponse(average));
    }

    @GetMapping("/max-creation-date")
    @Operation(summary = "Получить группу с максимальной датой создания",
            description = "Возвращает один объект музыкальной группы, у которого значение поля creationDate является максимальным")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Группа найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MusicBandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Коллекция пуста",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MusicBandResponse> getMusicBandWithMaxCreationDate() {
        return musicBandService.getBandWithMaxCreationDate()
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Коллекция пуста"));
    }

    @GetMapping("/search/name-contains")
    @Operation(summary = "Поиск групп по подстроке в названии",
            description = "Возвращает массив музыкальных групп, значение поля name которых содержит заданную подстроку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск выполнен успешно",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MusicBandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Параметр substring не указан",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MusicBandResponse>> getMusicBandsByNameSubstring(
            @Parameter(description = "Подстрока для поиска в названии группы", required = true)
            @RequestParam String substring) {
        if (substring == null || substring.isBlank()) {
            throw new BadRequestException("Параметр substring обязателен");
        }
        List<MusicBandResponse> bands = musicBandService.getBandsByNameSubstring(substring);
        return ResponseEntity.ok(bands);
    }
}
