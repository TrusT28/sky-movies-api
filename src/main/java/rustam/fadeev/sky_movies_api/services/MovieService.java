package rustam.fadeev.sky_movies_api.services;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.models.MovieCreateRequest;
import rustam.fadeev.sky_movies_api.models.MovieModel;
import rustam.fadeev.sky_movies_api.repositories.MovieRepository;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    public MovieService(MovieRepository movieRepository, RatingRepository ratingRepository) {
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    public Page<MovieModel> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieEntity> pageResult = movieRepository.findAll(pageable);

        return pageResult.map(MovieModel::new);
    }

    public void deleteMovieById(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            logger.info("The movie with id: {} is not found", movieId);
            throw new EntityNotFoundException("Movie with id" + movieId + " not found");
        }
        ratingRepository.deleteByMovieId(movieId);
        logger.info("The ratings for movie with id: {} were deleted", movieId);
        movieRepository.deleteById(movieId);
        logger.info("The movie with id: {} was deleted", movieId);
    }


    public MovieModel getMovieById(Long movieId) throws ResponseStatusException {
        logger.info("Looking for a movie with id: {}", movieId);
        MovieEntity result = movieRepository.findById(movieId).orElseThrow(() -> {
            logger.info("The movie with id: {} is not found", movieId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "A movie with movieId " + movieId + " is not found");
        });
        logger.info("The movie with id: {} is found", movieId);
        return new MovieModel(result);
    }

    public MovieModel getMovieByName(String movieName) throws ResponseStatusException {
        logger.info("Looking for a movie with name: {}", movieName);
        MovieEntity result = movieRepository.findByName(movieName).orElseThrow(() -> {
            logger.info("The movie with name: {} is not found", movieName);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "A movie with name " + movieName + " is not found");
        });
        logger.info("The movie with name: {} is found", movieName);
        return new MovieModel(result);
    }

    public MovieModel createMovie(MovieCreateRequest movieRequest) {
        logger.info("Creating movie with name: {}", movieRequest.name());
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setName(movieRequest.name());
        movieEntity.setReleaseDate(movieRequest.releaseDate());
        MovieEntity result = movieRepository.save(movieEntity);
        logger.info("Movie {} created with id: {}", movieRequest.name(), result.getId());
        return new MovieModel(result);
    }
}
