package rustam.fadeev.sky_movies_api.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;
import rustam.fadeev.sky_movies_api.entities.RatingId;
import rustam.fadeev.sky_movies_api.entities.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, RatingId> {
    Optional<RatingEntity> findByUserAndMovie(UserEntity user, MovieEntity movie);
    Optional<List<RatingEntity>> findByMovieId(Long movieId);
    Optional<List<RatingEntity>> findByUserId(Long userId);
    @Transactional
    @Modifying
    @Query("DELETE FROM RatingEntity r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RatingEntity r WHERE r.movie.id = :movieId")
    void deleteByMovieId(@Param("movieId") Long movieId);
}
