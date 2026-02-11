package c8102ea2a569.service1jetty.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "music_bands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicBandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Поле name не может быть null или пустым")
    @Column(nullable = false)
    private String name;

    @Embedded
    @Valid
    @NotNull(message = "Поле coordinates не может быть null")
    private CoordinatesEmbeddable coordinates;

    @NotNull(message = "Поле creationDate не может быть null")
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Positive(message = "Значение поля numberOfParticipants должно быть больше 0")
    @Column(name = "number_of_participants", nullable = false)
    private Long numberOfParticipants;

    @Positive(message = "Значение поля albumsCount должно быть больше 0")
    @Column(name = "albums_count", nullable = false)
    private Integer albumsCount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Поле genre не может быть null")
    @Column(nullable = false, length = 50)
    private MusicGenre genre;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "studio_id", nullable = false)
    @NotNull(message = "Поле studio не может быть null")
    private StudioEntity studio;
}
