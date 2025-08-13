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
import rustam.fadeev.sky_movies_api.models.*;
import rustam.fadeev.sky_movies_api.repositories.MovieRepository;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);

    public MovieStatisticsModel calculate_statistics(List<MovieRatingsModel> ratings) {
        logger.info("Calculating statistics...");
        double average_score = calculate_average(ratings);
        MovieStatisticsModel movieStatisticsModel = new MovieStatisticsModel(average_score);
        return movieStatisticsModel;
    }

    private double calculate_average(List<MovieRatingsModel> ratings) {
        logger.info("Calculating average...");
        if(ratings.isEmpty()) return 0.0;
        double scoreSum = ratings.stream()
                .mapToDouble(MovieRatingsModel::rating)
                .sum();
        double average = scoreSum / ratings.size();
        return average;
    }


    public boolean validateRatingScore(double rating) {
        double MIN_VALUE = 1.0;
        double MAX_VALUE = 5.0;
        return rating>=MIN_VALUE && rating <= MAX_VALUE;
    }

    public double normalizeRating(double rating) {
        return Math.round(rating * 2) / 2.0;
    }
}
