package rustam.fadeev.sky_movies_api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.models.SimpleMovieWithRatingModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
    Optional<MovieEntity> findByName(String name);
    Page<MovieEntity> findAll(Pageable pageable);
    @Query("""
        SELECT m, AVG(r.rating) AS avgRating
        FROM MovieEntity m
        LEFT JOIN RatingEntity r ON r.movie = m
        GROUP BY m
        ORDER BY avgRating DESC
    """)
    Page<Object[]> findTopRatedMovies(Pageable pageable);

}
