package rustam.fadeev.sky_movies_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    Optional<RatingEntity> findById(Long id);
    Optional<List<RatingEntity>> findByMovieId(Long movieId);
    Optional<List<RatingEntity>> findByUserId(Long userId);
}
