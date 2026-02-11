package c8102ea2a569.service1jetty.repository;

import c8102ea2a569.service1jetty.entity.StudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudioRepository extends JpaRepository<StudioEntity, Integer> {
    Optional<StudioEntity> findByName(String name);
}
