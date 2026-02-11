package c8102ea2a569.service1jetty.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesEmbeddable {

    @NotNull(message = "Поле x не может быть null")
    @Column(name = "coord_x", nullable = false)
    private Long x;

    @Max(value = 566, message = "Максимальное значение поля y: 566")
    @Column(name = "coord_y", nullable = false)
    private Double y;
}
