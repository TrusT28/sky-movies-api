package rustam.fadeev.sky_movies_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
