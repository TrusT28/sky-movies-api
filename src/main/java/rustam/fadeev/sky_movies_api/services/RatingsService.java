package rustam.fadeev.sky_movies_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;
import rustam.fadeev.sky_movies_api.entities.RatingId;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.MovieRatingsModel;
import rustam.fadeev.sky_movies_api.models.RatingCreateRequest;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.models.UserRatingsModel;
import rustam.fadeev.sky_movies_api.repositories.MovieRepository;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RatingsService {
    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RatingsService.class);
    public RatingsService(RatingRepository ratingRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<MovieRatingsModel> getRatingsOfMovieById(Long movieId) throws ResponseStatusException {
        logger.info("Looking for ratings of movieId: {}", movieId);
        List<RatingEntity> result = ratingRepository.findByMovieId(movieId).orElseThrow(() -> {
            logger.info("The ratings of movieId: {} are not found", movieId);
            return new ResponseStatusException(HttpStatus.NO_CONTENT, "The ratings of a movieId" + movieId + " are not found.");
        });
        logger.info("The ratings of movieId: {} are found", movieId);
        List<MovieRatingsModel> allRatings = new ArrayList<>(Collections.emptyList());
        result.forEach(ratingEntity -> allRatings.add(new MovieRatingsModel(ratingEntity)));
        return allRatings;
    }

    public List<UserRatingsModel> getRatingsOfUserById(Long userId) throws ResponseStatusException {
        logger.info("Looking for ratings of userId: {}", userId);
        List<RatingEntity> result = ratingRepository.findByMovieId(userId).orElseThrow(() -> {
            logger.info("The ratings of userId: {} are not found", userId);
            return new ResponseStatusException(HttpStatus.NO_CONTENT, "The ratings of a userId" + userId + " are not found.");
        });
        logger.info("The ratings of userId: {} are found", userId);
        List<UserRatingsModel> allRatings = new ArrayList<>(Collections.emptyList());
        result.forEach(ratingEntity -> allRatings.add(new UserRatingsModel(ratingEntity)));
        return allRatings;
    }

    public RatingModel getRatingById(Long id) throws ResponseStatusException {
        logger.info("Looking for a rating with id: {}", id);
        Optional<RatingEntity> result = ratingRepository.findById(id);
        if (result.isPresent()) {
            logger.info("The rating with id: {} is found", id);
            return new RatingModel(result.get());
        }
        else {
            logger.info("The rating with id: {} is not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "rating with a id" + id + " not found.");
        }
    }

    public RatingModel createRating(RatingCreateRequest request) {
        logger.info("Creating a new rating {} from userId: {} for a movieId: {}", request.rating(), request.userId(), request.movieId());
        // TODO validate rating correctness
        UserEntity user = userRepository.findById(request.userId()).orElseThrow(() -> {
            logger.info("A user with userId: {} is not found. Cannot create a rating", request.userId());
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with userId" + request.userId() + " is not found. Cannot create a rating");
        });

        MovieEntity movie = movieRepository.findById(request.movieId()).orElseThrow(() -> {
            logger.info("A movie with movieId: {} is not found. Cannot create a rating", request.movieId());
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "A movie with userId" + request.movieId() + " is not found. Cannot create a rating");
        });

        RatingEntity ratingEntity = ratingRepository.findByUserAndMovie(user, movie)
                .orElseGet(() -> {
                    logger.info("No rating of movieId {} by userId {} exists. Creating a new one...", movie.getId(), user.getId());
                    RatingEntity newRating = new RatingEntity();
                    newRating.setId(new RatingId(user.getId(), movie.getId()));
                    newRating.setUser(user);
                    newRating.setMovie(movie);
                    return newRating;
                });

        ratingEntity.setRating(request.rating());
        RatingEntity result = ratingRepository.save(ratingEntity);
        logger.info("Rating was created/updated with id: {}", result.getId());
        return new RatingModel(result);
    }
}
