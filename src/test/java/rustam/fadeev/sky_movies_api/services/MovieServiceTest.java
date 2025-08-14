package rustam.fadeev.sky_movies_api.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.models.MovieCreateRequest;
import rustam.fadeev.sky_movies_api.models.MovieModel;
import rustam.fadeev.sky_movies_api.repositories.MovieRepository;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private MovieService movieService;

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
        MovieEntity movieEntity1 = new MovieEntity();
        movieEntity1.setId(1L);
        movieEntity1.setName("Test Movie");
        MovieEntity movieEntity2 = new MovieEntity();
        movieEntity2.setId(2L);
        movieEntity2.setName("Test Movie 2");
        Page<MovieEntity> moviePage = new PageImpl<>(Arrays.asList(movieEntity1, movieEntity2));
        when(movieRepository.findAll(PageRequest.of(0, 10))).thenReturn(moviePage);

        //test
        Page<MovieModel> result = movieService.getAllMovies(0, 10);

        //verify
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Test Movie", result.getContent().get(0).name());
        assertEquals("Test Movie 2", result.getContent().get(1).name());
        verify(movieRepository, times(1)).findAll(PageRequest.of(0, 10));
    }


    @Test
    void deleteMovieById() {
        //init
        Long movieId = 1L;
        when(movieRepository.existsById(movieId)).thenReturn(true);

        //test
        movieService.deleteMovieById(movieId);

        //verify
        verify(ratingRepository, times(1)).deleteByMovieId(movieId);
        verify(movieRepository, times(1)).deleteById(movieId);
    }

    @Test
    void deleteMovieById_movieNotFound() {
        //init
        Long movieId = 1L;
        when(movieRepository.existsById(movieId)).thenReturn(false);

        //test
        assertThrows(EntityNotFoundException.class, () -> movieService.deleteMovieById(movieId));

        //verify
        verify(movieRepository, times(1)).existsById(movieId);
        verifyNoInteractions(ratingRepository);
    }

    @Test
    void getMovieById() {
        //init
        Long movieId = 5L;
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(movieId);
        movieEntity.setName("Test Movie");
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movieEntity));

        //test
        MovieModel result = movieService.getMovieById(movieId);

        //verify
        assertNotNull(result);
        assertEquals("Test Movie", result.name());
        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void getMovieById_movieNotFound() {
        //init
        Long movieId = 1L;
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        //test & verify
        assertThrows(RuntimeException.class, () -> movieService.getMovieById(movieId));
        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void getMovieByName() {
        //init
        String movieName = "Test Movie";
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setName(movieName);
        when(movieRepository.findByName(movieName)).thenReturn(Optional.of(movieEntity));

        //test
        MovieModel result = movieService.getMovieByName(movieName);

        //verify
        assertNotNull(result);
        assertEquals(movieName, result.name());
        verify(movieRepository, times(1)).findByName(movieName);
    }

    @Test
    void getMovieByName_movieNotFound() {
        //init
        String movieName = "Fake Movie";
        when(movieRepository.findByName(movieName)).thenReturn(Optional.empty());

        //test & verify
        assertThrows(RuntimeException.class, () -> movieService.getMovieByName(movieName));
        verify(movieRepository, times(1)).findByName(movieName);
    }

    @Test
    void createMovie() {
        //init
        MovieCreateRequest request = new MovieCreateRequest("New Movie", Date.valueOf("2020-02-20"));
        MovieEntity movieEntity = new MovieEntity();
        movieEntity.setId(1L);
        movieEntity.setName(request.name());
        when(movieRepository.save(any(MovieEntity.class))).thenReturn(movieEntity);

        //test
        MovieModel result = movieService.createMovie(request);

        //verify
        assertNotNull(result);
        assertEquals("New Movie", result.name());
        verify(movieRepository, times(1)).save(any(MovieEntity.class));
    }
}