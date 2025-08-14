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
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.models.*;
import rustam.fadeev.sky_movies_api.security.UserContext;
import rustam.fadeev.sky_movies_api.services.RatingsService;
import rustam.fadeev.sky_movies_api.services.UsersService;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @Mock
    private RatingsService ratingsService;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllUsers() {
        //init
        UserPrivateModel adminUser = new UserPrivateModel(null, null, "admin");
        UserContext.set(adminUser);

        Page<UserPrivateModel> mockPage = new PageImpl<>(Collections.singletonList(
                new UserPrivateModel(1L, "user1@example.com", "user1")
        ));
        when(usersService.getAllUsers(0, 10)).thenReturn(mockPage);

        //test
        PageResponse<UserPrivateModel> result = usersController.getAllUsers(0, 10);

        //verify
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("user1", result.content().get(0).username());
        verify(usersService, times(1)).getAllUsers(0, 10);
    }

    @Test
    void deleteUserById() {
        //init
        Long userId = 1L;
        UserPrivateModel adminUser = new UserPrivateModel(null, null, "admin");
        UserContext.set(adminUser);

        doNothing().when(usersService).deleteUserById(userId);

        //test
        assertDoesNotThrow(() -> usersController.deleteUserById(userId));

        //verify
        verify(usersService, times(1)).deleteUserById(userId);
    }

    @Test
    void deleteUserById_asUser() {
        //init
        Long userId = 1L;
        UserPrivateModel loggedInUser = new UserPrivateModel(userId, null, "user1");
        UserContext.set(loggedInUser);

        doNothing().when(usersService).deleteUserById(userId);

        //test
        assertDoesNotThrow(() -> usersController.deleteUserById(userId));

        //verify
        verify(usersService, times(1)).deleteUserById(userId);
    }

    @Test
    void deleteUserById_asAnotherUserShouldFail() {
        //init
        Long userId = 2L;
        UserPrivateModel loggedInUser = new UserPrivateModel(1L, null, "user1");
        UserContext.set(loggedInUser);

        //test & verify
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.deleteUserById(userId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Cannot delete another user", exception.getReason());
        verify(usersService, never()).deleteUserById(userId);
    }

    @Test
    void getUserById_asAdmin() {
        //init
        Long userId = 1L;
        UserPrivateModel adminUser = new UserPrivateModel(null, null, "admin");
        UserContext.set(adminUser);

        UserPrivateModel mockUser = new UserPrivateModel(userId, "user1@example.com", "user1");
        when(usersService.getUserById(userId)).thenReturn(mockUser);

        //test
        UserPrivateModel result = usersController.getUserById(userId).getBody();

        //verify
        assertNotNull(result);
        assertEquals("user1", result.username());
        verify(usersService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_asSelf() {
        //init
        Long userId = 1L;
        UserPrivateModel loggedInUser = new UserPrivateModel(userId, null, "user1");
        UserContext.set(loggedInUser);

        UserPrivateModel mockUser = new UserPrivateModel(userId, "user1@example.com", "user1");
        when(usersService.getUserById(userId)).thenReturn(mockUser);

        //test
        UserPrivateModel result = usersController.getUserById(userId).getBody();

        //verify
        assertNotNull(result);
        assertEquals("user1", result.username());
        verify(usersService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_asAnotherUser() {
        //init
        Long userId = 2L;
        UserPrivateModel loggedInUser = new UserPrivateModel(1L, null, "user1");
        UserContext.set(loggedInUser);

        //test & verify
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> usersController.getUserById(userId));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Cannot get info about another user", exception.getReason());
        verify(usersService, never()).getUserById(userId);
    }

    @Test
    void createUser() {
        //init
        UserCreateRequest request = new UserCreateRequest("user1", "user1@example.com", "password123");
        UserPrivateModel mockUser = new UserPrivateModel(1L, "user1@example.com", "user1");
        when(usersService.createUser(request)).thenReturn(mockUser);

        //test
        UserPrivateModel result = usersController.createUser(request);

        //verify
        assertNotNull(result);
        assertEquals("user1", result.username());
        verify(usersService, times(1)).createUser(request);
    }

    @Test
    void getRatingsOfUserById() {
        //init
        Long userId = 1L;
        MovieModel movie = new MovieModel(1L,"Test Movie", Date.valueOf("2020-02-20"));
        List<UserRatingsModel> mockRatings = Collections.singletonList(new UserRatingsModel(movie, 4.5));
        when(ratingsService.getRatingsOfUserById(userId)).thenReturn(mockRatings);

        //test
        List<UserRatingsModel> result = usersController.getRatingsOfUserById(userId);

        //verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).movie().name());
        verify(ratingsService, times(1)).getRatingsOfUserById(userId);
    }
}