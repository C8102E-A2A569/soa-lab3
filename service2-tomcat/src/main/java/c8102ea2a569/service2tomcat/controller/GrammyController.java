package c8102ea2a569.service2tomcat.controller;

import c8102ea2a569.service2tomcat.dto.ErrorResponse;
import c8102ea2a569.service2tomcat.dto.RewardResponse;
import c8102ea2a569.service2tomcat.exception.ValidationException;
import c8102ea2a569.service2tomcat.service.GrammyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grammy")
@Tag(name = "Grammy Awards")
@RequiredArgsConstructor
public class GrammyController {

    private final GrammyService grammyService;

    @DeleteMapping("/band/{band-id}/participants/remove")
    @Operation(
            summary = "Удалить участника из группы",
            description = "Удаляет одного участника из музыкальной группы через вызов API Service 1. " +
                    "Если участников больше нет, операция невозможна"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Участник успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Группа не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Невозможно удалить участника",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Service 1 недоступен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> removeParticipant(
            @Parameter(description = "ID музыкальной группы", required = true)
            @PathVariable("band-id") Integer bandId) {

        if (bandId <= 0) {
            throw new ValidationException("ID должен быть больше 0");
        }

        grammyService.removeParticipant(bandId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/band/{band-id}/reward/{genre}")
    @Operation(
            summary = "Наградить группу Grammy",
            description = "Награждает музыкальную группу как лучшую в указанном жанре. " +
                    "Проверяет соответствие жанра группы через вызов API Service 1 и создаёт запись о награде в БД"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Группа успешно награждена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RewardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Группа не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Жанр группы не соответствует указанному жанру награды",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Service 1 недоступен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RewardResponse> rewardBand(
            @Parameter(description = "ID музыкальной группы", required = true)
            @PathVariable("band-id") Integer bandId,

            @Parameter(description = "Жанр музыки для награждения", required = true)
            @PathVariable String genre) {

        if (bandId <= 0) {
            throw new ValidationException("ID группы должен быть больше 0");
        }

        if (genre == null || genre.isBlank()) {
            throw new ValidationException("Жанр не может быть пустым");
        }

        RewardResponse response = grammyService.rewardBand(bandId, genre);
        return ResponseEntity.status(201).body(response);
    }
}
