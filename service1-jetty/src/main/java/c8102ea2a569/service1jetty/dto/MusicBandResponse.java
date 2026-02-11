package c8102ea2a569.service1jetty.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicBandResponse {
    private Integer id;
    private String name;
    private CoordinatesDTO coordinates;
    private LocalDateTime creationDate;
    private Long numberOfParticipants;
    private Integer albumsCount;
    private String genre;
    private StudioDTO studio;
}
