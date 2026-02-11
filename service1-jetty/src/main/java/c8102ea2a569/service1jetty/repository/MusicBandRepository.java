package c8102ea2a569.service1jetty.repository;

import c8102ea2a569.service1jetty.entity.MusicBandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicBandRepository extends JpaRepository<MusicBandEntity, Integer>,
        JpaSpecificationExecutor<MusicBandEntity> {

    @Query("SELECT AVG(b.albumsCount) FROM MusicBandEntity b")
    Double findAverageAlbumsCount();

    Optional<MusicBandEntity> findFirstByOrderByCreationDateDesc();

    List<MusicBandEntity> findByNameContainingIgnoreCase(String substring);
}
