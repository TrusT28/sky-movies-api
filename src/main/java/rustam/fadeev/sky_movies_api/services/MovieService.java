package rustam.fadeev.sky_movies_api.services;

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
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Page<MovieModel> getAllMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovieEntity> pageResult = movieRepository.findAll(pageable);

        return pageResult.map(MovieModel::new);
    }

    public MovieModel getMovieById(Long movieId) throws ResponseStatusException {
        logger.info("Looking for a movie with id: {}", movieId);
        Optional<MovieEntity> result = movieRepository.findById(movieId);
        if (result.isPresent()) {
            logger.info("The movie with id: {} is found", movieId);
            return new MovieModel(result.get());
        }
        else {
            logger.info("The movie with id: {} is not found", movieId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie with a id" + movieId + " not found.");
        }
    }

    public MovieModel getMovieByName(String movieName) throws ResponseStatusException {
        logger.info("Looking for a movie with name: {}", movieName);
        Optional<MovieEntity> result = movieRepository.findByName(movieName);
        if (result.isPresent()) {
            logger.info("The movie with name: {} is found", movieName);
            return new MovieModel(result.get());
        }
        else {
            logger.info("The movie with name: {} is not found", movieName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie with a name" + movieName + " not found.");
        }
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
