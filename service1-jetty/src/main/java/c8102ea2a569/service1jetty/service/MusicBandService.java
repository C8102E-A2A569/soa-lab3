package c8102ea2a569.service1jetty.service;

import c8102ea2a569.service1jetty.dto.*;
import c8102ea2a569.service1jetty.entity.*;
import c8102ea2a569.service1jetty.repository.MusicBandRepository;
import c8102ea2a569.service1jetty.repository.StudioRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicBandService {

    private final MusicBandRepository bandRepository;
    private final StudioRepository studioRepository;

    @Transactional
    public MusicBandResponse createBand(MusicBandDTO dto) {
        StudioEntity studio = findOrCreateStudio(dto.getStudio());

        MusicBandEntity band = new MusicBandEntity();
        band.setName(dto.getName());
        band.setCoordinates(toEmbeddable(dto.getCoordinates()));
        band.setCreationDate(LocalDateTime.now());
        band.setNumberOfParticipants(dto.getNumberOfParticipants());
        band.setAlbumsCount(dto.getAlbumsCount());
        band.setGenre(MusicGenre.valueOf(dto.getGenre()));
        band.setStudio(studio);

        MusicBandEntity saved = bandRepository.save(band);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Optional<MusicBandResponse> getBandById(Integer id) {
        return bandRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public Optional<MusicBandResponse> updateBand(Integer id, MusicBandDTO dto) {
        return bandRepository.findById(id).map(band -> {
            StudioEntity studio = findOrCreateStudio(dto.getStudio());

            band.setName(dto.getName());
            band.setCoordinates(toEmbeddable(dto.getCoordinates()));
            band.setNumberOfParticipants(dto.getNumberOfParticipants());
            band.setAlbumsCount(dto.getAlbumsCount());
            band.setGenre(MusicGenre.valueOf(dto.getGenre()));
            band.setStudio(studio);

            return toResponse(bandRepository.save(band));
        });
    }

    @Transactional
    public void deleteBand(Integer id) {
        bandRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MusicBandResponse> getAllBands(String sortBy, String filterBy, Integer page, Integer size) {
        Sort sort = buildSort(sortBy);
        Specification<MusicBandEntity> spec = buildSpecification(filterBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MusicBandEntity> result = bandRepository.findAll(spec, pageable);
        return result.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public long getTotalCount(String filterBy) {
        Specification<MusicBandEntity> spec = buildSpecification(filterBy);
        return bandRepository.count(spec);
    }

    @Transactional(readOnly = true)
    public Double getAverageAlbumsCount() {
        Double avg = bandRepository.findAverageAlbumsCount();
        return avg != null ? avg : 0.0;
    }

    @Transactional(readOnly = true)
    public Optional<MusicBandResponse> getBandWithMaxCreationDate() {
        return bandRepository.findFirstByOrderByCreationDateDesc().map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<MusicBandResponse> getBandsByNameSubstring(String substring) {
        return bandRepository.findByNameContainingIgnoreCase(substring)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private StudioEntity findOrCreateStudio(StudioDTO dto) {
        return studioRepository.findByName(dto.getName())
                .orElseGet(() -> {
                    StudioEntity newStudio = new StudioEntity();
                    newStudio.setName(dto.getName());
                    newStudio.setAddress(dto.getAddress());
                    return studioRepository.save(newStudio);
                });
    }

    private Sort buildSort(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String direction = "ASC";
        String field = sortBy;

        if (sortBy.startsWith("-")) {
            direction = "DESC";
            field = sortBy.substring(1);
        }

        field = mapFieldToEntity(field);
        return Sort.by(Sort.Direction.fromString(direction), field);
    }

    private Specification<MusicBandEntity> buildSpecification(String filterBy) {
        return (root, query, cb) -> {
            if (filterBy == null || filterBy.isBlank()) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            String[] filters = filterBy.split(",");

            for (String filter : filters) {
                String[] parts = filter.split("=", 2);
                if (parts.length == 2) {
                    String field = mapFieldToEntity(parts[0].trim());
                    String value = parts[1].trim();

                    if (field.contains(".")) {
                        String[] nested = field.split("\\.");
                        if (nested[0].equals("coordinates")) {
                            if (nested[1].equals("x")) {
                                try {
                                    predicates.add(cb.equal(root.get("coordinates").get("x"), Long.parseLong(value)));
                                } catch (NumberFormatException ignored) {
                                }
                            } else if (nested[1].equals("y")) {
                                try {
                                    predicates.add(cb.equal(root.get("coordinates").get("y"), Double.parseDouble(value)));
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        } else if (nested[0].equals("studio")) {
                            if (nested[1].equals("name")) {
                                predicates.add(cb.like(cb.lower(root.get("studio").get("name")),
                                        "%" + value.toLowerCase() + "%"));
                            } else if (nested[1].equals("address")) {
                                predicates.add(cb.like(cb.lower(root.get("studio").get("address")),
                                        "%" + value.toLowerCase() + "%"));
                            }
                        }
                    } else {
                        switch (field) {
                            case "id":
                                try {
                                    predicates.add(cb.equal(root.get("id"), Integer.parseInt(value)));
                                } catch (NumberFormatException ignored) {
                                }
                                break;
                            case "name":
                                predicates.add(cb.like(cb.lower(root.get("name")), "%" + value.toLowerCase() + "%"));
                                break;
                            case "numberOfParticipants":
                                try {
                                    predicates.add(cb.equal(root.get("numberOfParticipants"), Long.parseLong(value)));
                                } catch (NumberFormatException ignored) {
                                }
                                break;
                            case "albumsCount":
                                try {
                                    predicates.add(cb.equal(root.get("albumsCount"), Integer.parseInt(value)));
                                } catch (NumberFormatException ignored) {
                                }
                                break;
                            case "genre":
                                try {
                                    predicates.add(cb.equal(root.get("genre"), MusicGenre.valueOf(value)));
                                } catch (IllegalArgumentException ignored) {
                                }
                                break;
                        }
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String mapFieldToEntity(String field) {
        Map<String, String> fieldMap = Map.of(
                "coord_x", "coordinates.x",
                "coord_y", "coordinates.y",
                "studio_name", "studio.name",
                "studio_address", "studio.address"
        );
        return fieldMap.getOrDefault(field, field);
    }

    private CoordinatesEmbeddable toEmbeddable(CoordinatesDTO dto) {
        return new CoordinatesEmbeddable(dto.getX(), dto.getY());
    }

    private CoordinatesDTO toCoordinatesDTO(CoordinatesEmbeddable embeddable) {
        return new CoordinatesDTO(embeddable.getX(), embeddable.getY());
    }

    private StudioDTO toStudioDTO(StudioEntity entity) {
        return new StudioDTO(entity.getName(), entity.getAddress());
    }

    private MusicBandResponse toResponse(MusicBandEntity entity) {
        MusicBandResponse response = new MusicBandResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCoordinates(toCoordinatesDTO(entity.getCoordinates()));
        response.setCreationDate(entity.getCreationDate());
        response.setNumberOfParticipants(entity.getNumberOfParticipants());
        response.setAlbumsCount(entity.getAlbumsCount());
        response.setGenre(entity.getGenre().name());
        response.setStudio(toStudioDTO(entity.getStudio()));
        return response;
    }
}