package rustam.fadeev.sky_movies_api.services;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;
import rustam.fadeev.sky_movies_api.entities.RatingId;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.*;
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
    private final StatisticsService statisticsService;

    private static final Logger logger = LoggerFactory.getLogger(RatingsService.class);

    public RatingsService(RatingRepository ratingRepository, MovieRepository movieRepository, UserRepository userRepository, StatisticsService statisticsService) {
        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.statisticsService = statisticsService;
    }
    public DetailedMovieWithRatingModel getRatingsOfMovieById(Long movieId) throws ResponseStatusException {
        logger.info("Looking for ratings of movieId: {}", movieId);
        MovieEntity movieEntity = movieRepository.findById(movieId).orElseThrow(() -> {
            logger.info("A movie with movieId: {} is not found. Cannot create a rating", movieId);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "A movie with userId" + movieId + " is not found. Cannot create a rating");
        });
        List<RatingEntity> result = ratingRepository.findByMovieId(movieId).orElseThrow(() -> {
            logger.info("The ratings of movieId: {} are not found", movieId);
            return new ResponseStatusException(HttpStatus.NO_CONTENT, "The ratings of a movieId" + movieId + " are not found.");
        });
        logger.info("The ratings of movieId: {} are found", movieId);
        List<MovieRatingsModel> allRatings = new ArrayList<>(Collections.emptyList());
        result.forEach(ratingEntity -> {
            allRatings.add(new MovieRatingsModel(ratingEntity));
        });
        MovieStatisticsModel movieStatisticsModel = statisticsService.calculate_statistics(allRatings);
        MovieModel movieModel = new MovieModel(movieEntity);
        return new DetailedMovieWithRatingModel(movieModel, movieStatisticsModel, allRatings);
    }

    public Page<SimpleMovieWithRatingModel> getTopRatedMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> pageResult = movieRepository.findTopRatedMovies(pageable);

        return pageResult.map(row -> {
            MovieEntity movie = (MovieEntity) row[0];
            Double avgScore = (Double) row[1];
            return new SimpleMovieWithRatingModel(
                    new MovieModel(movie),
                    new MovieStatisticsModel(avgScore)
            );
        });
    }

    public List<UserRatingsModel> getRatingsOfUserById(Long userId) throws ResponseStatusException {
        logger.info("Looking for ratings of userId: {}", userId);
        List<RatingEntity> result = ratingRepository.findByUserId(userId).orElseThrow(() -> {
            logger.info("The ratings of userId: {} are not found", userId);
            return new ResponseStatusException(HttpStatus.NO_CONTENT, "The ratings of a userId" + userId + " are not found.");
        });
        logger.info("The ratings of userId: {} are found", userId);
        List<UserRatingsModel> allRatings = new ArrayList<>(Collections.emptyList());
        result.forEach(ratingEntity -> allRatings.add(new UserRatingsModel(ratingEntity)));
        return allRatings;
    }


    public void deleteRatingById(Long movieId, Long userId) throws EntityNotFoundException {
        logger.info("Deleting a rating for movieId {} by userId {}", movieId, userId);
        RatingId id = new RatingId(userId, movieId);
        if (!ratingRepository.existsById(id)) {
            logger.info("Rating for movieId {} by userId {} was not found", movieId, userId);
            throw new EntityNotFoundException("Rating not found");
        }
        ratingRepository.deleteById(id);
        logger.info("Rating for movieId {} by userId {} was successfully deleted", movieId, userId);
    }

    public RatingModel getRatingById(Long movieId, Long userId) throws ResponseStatusException {
        logger.info("Getting a rating for movieId {} by userId {}", movieId, userId);
        RatingId id = new RatingId(userId, movieId);
        RatingEntity result = ratingRepository.findById(id).orElseThrow(() -> {
            logger.info("Rating for movieId {} by userId {} was not found", movieId, userId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "A rating for movieId " + movieId + " by userId " + userId + "is not found");
        });
        return new RatingModel(result);
    }

    public RatingModel createRating(RatingCreateRequest request) {
        logger.info("Creating a new rating {} from userId: {} for a movieId: {}", request.rating(), request.userId(), request.movieId());
        UserEntity user = userRepository.findById(request.userId()).orElseThrow(() -> {
            logger.info("A user with userId: {} is not found. Cannot create a rating", request.userId());
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with userId" + request.userId() + " is not found. Cannot create a rating");
        });

        MovieEntity movie = movieRepository.findById(request.movieId()).orElseThrow(() -> {
            logger.info("A movie with movieId: {} is not found. Cannot create a rating", request.movieId());
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "A movie with userId" + request.movieId() + " is not found. Cannot create a rating");
        });

        if (!statisticsService.validateRatingScore(request.rating())) {
            logger.info("A rating value {} is invalid. Cannot create a rating", request.rating());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A rating value " + request.rating() + " is invalid. Cannot create a rating");
        }

        RatingEntity ratingEntity = ratingRepository.findByUserAndMovie(user, movie)
                .orElseGet(() -> {
                    logger.info("No rating of movieId {} by userId {} exists. Creating a new one...", movie.getId(), user.getId());
                    RatingEntity newRating = new RatingEntity();
                    newRating.setId(new RatingId(user.getId(), movie.getId()));
                    newRating.setUser(user);
                    newRating.setMovie(movie);
                    return newRating;
                });

        double normalizedRating = statisticsService.normalizeRating(request.rating());
        ratingEntity.setRating(normalizedRating);
        RatingEntity result = ratingRepository.save(ratingEntity);
        logger.info("Rating was created/updated with id: {}", result.getId());
        return new RatingModel(result);
    }
}
