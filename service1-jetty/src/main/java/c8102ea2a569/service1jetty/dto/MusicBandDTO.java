package c8102ea2a569.service1jetty.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicBandDTO {

    @NotBlank(message = "Поле name не может быть null или пустым")
    private String name;

    @NotNull(message = "Поле coordinates не может быть null")
    @Valid
    private CoordinatesDTO coordinates;  // ← DTO, не Embeddable!

    @Positive(message = "Поле numberOfParticipants должно быть больше 0")
    private Long numberOfParticipants;

    @Positive(message = "Поле albumsCount должно быть больше 0")
    private Integer albumsCount;

    @NotNull(message = "Поле genre не может быть null")
    private String genre;

    @NotNull(message = "Поле studio не может быть null")
    @Valid
    private StudioDTO studio;
}
