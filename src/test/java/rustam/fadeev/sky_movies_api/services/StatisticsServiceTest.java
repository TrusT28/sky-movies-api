package rustam.fadeev.sky_movies_api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rustam.fadeev.sky_movies_api.models.MovieRatingsModel;
import rustam.fadeev.sky_movies_api.models.MovieStatisticsModel;
import rustam.fadeev.sky_movies_api.models.UserPublicModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StatisticsServiceTest {

    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsService();
    }

    @Test
    void calculate_statistics() {
        //init
        UserPublicModel mockUser = mock(UserPublicModel.class);
        List<MovieRatingsModel> ratings = Arrays.asList(
                new MovieRatingsModel(mockUser, 4.0),
                new MovieRatingsModel(mockUser, 5.0),
                new MovieRatingsModel(mockUser, 3.0)
        );

        //test
        MovieStatisticsModel result = statisticsService.calculate_statistics(ratings);

        //verify
        assertNotNull(result);
        assertEquals(4.0, result.average_score());
    }

    @Test
    void calculate_statistics_emptyList() {
        //init
        List<MovieRatingsModel> ratings = Collections.emptyList();

        //test
        MovieStatisticsModel result = statisticsService.calculate_statistics(ratings);

        //verify
        assertNotNull(result);
        assertEquals(0.0, result.average_score());
    }

    @Test
    void validateRatingScore() {
        //test & verify
        assertTrue(statisticsService.validateRatingScore(3.5));
        assertTrue(statisticsService.validateRatingScore(1.0));
        assertTrue(statisticsService.validateRatingScore(5.0));
        assertFalse(statisticsService.validateRatingScore(0.5));
        assertFalse(statisticsService.validateRatingScore(5.5));
    }

    @Test
    void normalizeRating() {
        //test & verify
        assertEquals(3.5, statisticsService.normalizeRating(3.49));
        assertEquals(3.5, statisticsService.normalizeRating(3.51));
        assertEquals(1.0, statisticsService.normalizeRating(1.0));
        assertEquals(5.0, statisticsService.normalizeRating(5.0));
    }
}