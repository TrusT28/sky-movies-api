package rustam.fadeev.sky_movies_api.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import rustam.fadeev.sky_movies_api.models.*;
import rustam.fadeev.sky_movies_api.security.PasswordAuthInterceptor;
import rustam.fadeev.sky_movies_api.services.MovieService;
import rustam.fadeev.sky_movies_api.services.RatingsService;

import java.sql.Date;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MoviesControllerTest {

    @Mock
    private MovieService movieService;

    @Mock
    private RatingsService ratingsService;

    @Mock
    private PasswordAuthInterceptor passwordAuthInterceptor;

    @InjectMocks
    private MoviesController moviesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllMovies() {
        //init
        Page<MovieModel> mockPage = new PageImpl<>(Collections.singletonList(new MovieModel(1L, "Test Movie",  Date.valueOf("2020-02-20"))));
        when(movieService.getAllMovies(0, 10)).thenReturn(mockPage);

        //test
        PageResponse<MovieModel> result = moviesController.getAllMovies(0, 10);

        //verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.content().get(0).name());
        verify(movieService, times(1)).getAllMovies(0, 10);
    }

    @Test
    void getMovieById() {
        //init
        MovieModel mockMovie = new MovieModel(1L, "Test Movie", Date.valueOf("2020-02-20"));
        when(movieService.getMovieById(1L)).thenReturn(mockMovie);

        //test
        MovieModel result = moviesController.getMovieById(1L);

        //verify
        assertNotNull(result);
        assertEquals("Test Movie", result.name());
        verify(movieService, times(1)).getMovieById(1L);
    }

    @Test
    void deleteMovieById() throws Exception {
        //init
        doNothing().when(movieService).deleteMovieById(1L);

        //mock auth
        when(passwordAuthInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        //test
        moviesController.deleteMovieById(1L);

        //verify
        verify(movieService, times(1)).deleteMovieById(1L);
    }

    @Test
    void getRatingsOfMovieById() {
        //init
        UserPublicModel mockUser = mock(UserPublicModel.class);
        DetailedMovieWithRatingModel mockRatings = new DetailedMovieWithRatingModel(
                new MovieModel(1L, "Test Movie", Date.valueOf("2020-02-20")),
                new MovieStatisticsModel(4.5),
                Collections.singletonList(new MovieRatingsModel(mockUser, 4.5))
        );
        when(ratingsService.getRatingsOfMovieById(1L)).thenReturn(mockRatings);

        //test
        DetailedMovieWithRatingModel result = moviesController.getRatingsOfMovieById(1L);

        //verify
        assertNotNull(result);
        assertEquals("Test Movie", result.movie().name());
        assertEquals(4.5, result.statistics().average_score());
        verify(ratingsService, times(1)).getRatingsOfMovieById(1L);
    }

    @Test
    void getTopRatedMovies() {
        //init
        Page<SimpleMovieWithRatingModel> mockPage = new PageImpl<>(Collections.singletonList(
                new SimpleMovieWithRatingModel(
                        new MovieModel(1L, "Top Movie", Date.valueOf("2020-02-20")),
                        new MovieStatisticsModel(4.8)
                )
        ));
        when(ratingsService.getTopRatedMovies(0, 1)).thenReturn(mockPage);

        //test
        PageResponse<SimpleMovieWithRatingModel> result = moviesController.getTopRatedMovies(0, 1);

        //verify
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("Top Movie", result.content().get(0).movie().name());
        assertEquals(4.8, result.content().get(0).statistics().average_score());
        verify(ratingsService, times(1)).getTopRatedMovies(0, 1);
    }

    @Test
    void getMovieByName() {
        //init
        MovieModel mockMovie = new MovieModel(1L, "Test Movie", Date.valueOf("2020-02-20"));
        when(movieService.getMovieByName("Test Movie")).thenReturn(mockMovie);

        //test
        MovieModel result = moviesController.getMovieByName("Test Movie");

        //verify
        assertNotNull(result);
        assertEquals("Test Movie", result.name());
        verify(movieService, times(1)).getMovieByName("Test Movie");
    }

    @Test
    void createMovie() throws Exception {
        //init
        MovieCreateRequest request = new MovieCreateRequest("New Movie",  Date.valueOf("2020-02-20"));
        MovieModel mockMovie = new MovieModel(1L, "New Movie",  Date.valueOf("2020-02-20"));
        when(movieService.createMovie(request)).thenReturn(mockMovie);

        //mock auth
        when(passwordAuthInterceptor.preHandle(any(), any(), any())).thenReturn(true);

        //test
        MovieModel result = moviesController.createMovie(request);

        //verify
        assertNotNull(result);
        assertEquals("New Movie", result.name());
        verify(movieService, times(1)).createMovie(request);
    }
}