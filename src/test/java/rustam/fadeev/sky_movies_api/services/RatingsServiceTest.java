package rustam.fadeev.sky_movies_api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;
import rustam.fadeev.sky_movies_api.entities.RatingId;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.*;
import rustam.fadeev.sky_movies_api.repositories.MovieRepository;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

import java.sql.Date;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingsServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private RatingsService ratingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRatingsOfMovieById() {
        //init
        Long movieId = 1L;
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(movieId);
        movieEntity.setName("Test Movie");
        movieEntity.setReleaseDate(Date.valueOf("2020-02-20"));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("TestUser");

        RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.setId(new RatingId(userEntity.getId(), movieId));
        ratingEntity.setRating(4.5);
        ratingEntity.setUser(userEntity);
        ratingEntity.setMovie(movieEntity);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movieEntity));
        when(ratingRepository.findByMovieId(movieId)).thenReturn(Optional.of(Collections.singletonList(ratingEntity)));
        when(statisticsService.calculate_statistics(anyList())).thenReturn(new MovieStatisticsModel(4.5));

        //test
        DetailedMovieWithRatingModel result = ratingsService.getRatingsOfMovieById(movieId);

        //verify
        assertNotNull(result);
        assertEquals("Test Movie", result.movie().name());
        assertEquals(4.5, result.statistics().average_score());
        verify(movieRepository, times(1)).findById(movieId);
        verify(ratingRepository, times(1)).findByMovieId(movieId);
        verify(statisticsService, times(1)).calculate_statistics(anyList());
    }

    @Test
    void getRatingsOfMovieById_movieNotFound() {
        //init
        Long movieId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        //test & verify
        assertThrows(RuntimeException.class, () -> ratingsService.getRatingsOfMovieById(movieId));
        verify(movieRepository, times(1)).findById(movieId);
        verifyNoInteractions(ratingRepository, statisticsService);
    }

    @Test
    void getTopRatedMovies() {
        //init
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(1L);
        movieEntity.setName("Top Movie");

        Object[] row = new Object[]{movieEntity, 4.8};
        Page<Object[]> pageResult = new PageImpl<>(Collections.singletonList(row));
        when(movieRepository.findTopRatedMovies(PageRequest.of(0, 10))).thenReturn(pageResult);

        //test
        Page<SimpleMovieWithRatingModel> result = ratingsService.getTopRatedMovies(0, 10);

        //verify
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Top Movie", result.getContent().get(0).movie().name());
        assertEquals(4.8, result.getContent().get(0).statistics().average_score());
        verify(movieRepository, times(1)).findTopRatedMovies(PageRequest.of(0, 10));
    }

    @Test
    void deleteRatingById() {
        //init
        Long movieId = 1L;
        Long userId = 2L;
        RatingId ratingId = new RatingId(userId, movieId);
        when(ratingRepository.existsById(ratingId)).thenReturn(true);

        //test
        ratingsService.deleteRatingById(movieId, userId);

        //verify
        verify(ratingRepository, times(1)).existsById(ratingId);
        verify(ratingRepository, times(1)).deleteById(ratingId);
    }

    @Test
    void deleteRatingById_ratingNotFound() {
        //init
        Long movieId = 1L;
        Long userId = 2L;
        RatingId ratingId = new RatingId(userId, movieId);
        when(ratingRepository.existsById(ratingId)).thenReturn(false);

        //test
        ratingsService.deleteRatingById(movieId, userId);

        //verify
        verify(ratingRepository, times(1)).existsById(ratingId);
        verify(ratingRepository, never()).deleteById(ratingId);
    }

    @Test
    void createRating() {
        //init
        RatingCreateRequest request = new RatingCreateRequest(1L, 2L, 4.5);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("TestUser");

        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(2L);
        movieEntity.setName("Test Movie");

        RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.setId(new RatingId(userEntity.getId(), movieEntity.getId()));
        ratingEntity.setRating(4.5);
        ratingEntity.setUser(userEntity);
        ratingEntity.setMovie(movieEntity);

        when(userRepository.findById(request.userId())).thenReturn(Optional.of(userEntity));
        when(movieRepository.findById(request.movieId())).thenReturn(Optional.of(movieEntity));
        when(statisticsService.validateRatingScore(request.rating())).thenReturn(true);
        when(statisticsService.normalizeRating(request.rating())).thenReturn(4.5);
        when(ratingRepository.findByUserAndMovie(userEntity, movieEntity)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(RatingEntity.class))).thenReturn(ratingEntity);

        //test
        RatingModel result = ratingsService.createRating(request);

        //verify
        assertNotNull(result);
        assertEquals(4.5, result.rating());
        assertEquals("Test Movie", result.movie().name());
        verify(userRepository, times(1)).findById(request.userId());
        verify(movieRepository, times(1)).findById(request.movieId());
        verify(statisticsService, times(1)).validateRatingScore(request.rating());
        verify(statisticsService, times(1)).normalizeRating(request.rating());
        verify(ratingRepository, times(1)).save(any(RatingEntity.class));
    }
}