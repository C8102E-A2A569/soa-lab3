package c8102ea2a569.service1jetty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudioDTO {
    @NotBlank(message = "Поле name не может быть null или пустым")
    private String name;
    private String address;
}
