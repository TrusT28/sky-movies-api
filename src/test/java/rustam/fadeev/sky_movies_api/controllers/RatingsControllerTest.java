package rustam.fadeev.sky_movies_api.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.models.MovieModel;
import rustam.fadeev.sky_movies_api.models.RatingCreateRequest;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.security.UserContext;
import rustam.fadeev.sky_movies_api.services.RatingsService;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.models.UserPublicModel;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingsControllerTest {

    @Mock
    private RatingsService ratingsService;

    @InjectMocks
    private RatingsController ratingsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void createRating_asAdmin() {
        //init
        RatingCreateRequest request = new RatingCreateRequest(1L, 2L, 4.5);
        UserPrivateModel adminUser = new UserPrivateModel(null, null, "admin");
        UserContext.set(adminUser);

        MovieModel movie = new MovieModel(1L, "Test Movie", Date.valueOf("2020-02-20"));
        UserPublicModel user = new UserPublicModel("user2");
        RatingModel mockRating = new RatingModel(movie, user, 4.5);
        when(ratingsService.createRating(request)).thenReturn(mockRating);

        //test
        ResponseEntity<RatingModel> response = ratingsController.createRating(request);

        //verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4.5, response.getBody().rating());
        assertEquals("Test Movie", response.getBody().movie().name());
        assertEquals("user2", response.getBody().user().username());
        verify(ratingsService, times(1)).createRating(request);
    }

    @Test
    void createRating_asSelf() {
        //init
        Long userId = 1L;
        Long movieId = 2L;
        RatingCreateRequest request = new RatingCreateRequest(movieId, userId, 4.5);
        UserPrivateModel loggedInUser = new UserPrivateModel(userId, null, "user1");
        UserContext.set(loggedInUser);

        MovieModel movie = new MovieModel(movieId, "Test Movie", Date.valueOf("2020-02-20"));
        UserPublicModel user = new UserPublicModel("user1");
        RatingModel mockRating = new RatingModel(movie, user, 4.5);
        when(ratingsService.createRating(request)).thenReturn(mockRating);

        //test
        ResponseEntity<RatingModel> response = ratingsController.createRating(request);

        //verify
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4.5, response.getBody().rating());
        assertEquals("Test Movie", response.getBody().movie().name());
        assertEquals("user1", response.getBody().user().username());
        verify(ratingsService, times(1)).createRating(request);
    }

    @Test
    void createRating_asAnotherUserShouldFail() {
        //init
        RatingCreateRequest request = new RatingCreateRequest(2L, 2L, 4.5);
        UserPrivateModel loggedInUser = new UserPrivateModel(1L, null, "user1");
        UserContext.set(loggedInUser);

        //test & verify
        ResponseEntity<RatingModel> response = ratingsController.createRating(request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(ratingsService, never()).createRating(request);
    }

    @Test
    void deleteRating_asAdmin() {
        //init
        Long movieId = 1L;
        Long userId = 2L;
        UserPrivateModel adminUser = new UserPrivateModel(null, null, "admin");
        UserContext.set(adminUser);

        doNothing().when(ratingsService).deleteRatingById(movieId, userId);

        //test
        assertDoesNotThrow(() -> ratingsController.deleteRating(movieId, userId));

        //verify
        verify(ratingsService, times(1)).deleteRatingById(movieId, userId);
    }

    @Test
    void deleteRating_asSelf() {
        //init
        Long movieId = 1L;
        Long userId = 1L;
        UserPrivateModel loggedInUser = new UserPrivateModel(userId, null, "user1");
        UserContext.set(loggedInUser);

        doNothing().when(ratingsService).deleteRatingById(movieId, userId);

        //test
        assertDoesNotThrow(() -> ratingsController.deleteRating(movieId, userId));

        //verify
        verify(ratingsService, times(1)).deleteRatingById(movieId, userId);
    }

    @Test
    void deleteRating_asAnotherUserShouldFail() {
        //init
        Long movieId = 1L;
        Long userId = 2L;
        UserPrivateModel loggedInUser = new UserPrivateModel(1L, null, "user1");
        UserContext.set(loggedInUser);

        //test & verify
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> ratingsController.deleteRating(movieId, userId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You cannot delete another user's rating", exception.getReason());
        verify(ratingsService, never()).deleteRatingById(movieId, userId);
    }
}