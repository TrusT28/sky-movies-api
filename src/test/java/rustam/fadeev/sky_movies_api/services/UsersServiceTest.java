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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private UsersService usersService;

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
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("TestUser");
        Page<UserEntity> userPage = new PageImpl<>(Collections.singletonList(userEntity));
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);

        //test
        Page<UserPrivateModel> result = usersService.getAllUsers(0, 10);

        //verify
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("TestUser", result.getContent().get(0).username());
        verify(userRepository, times(1)).findAll(PageRequest.of(0, 10));
    }

    @Test
    void deleteUserById() {
        //init
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        //test
        usersService.deleteUserById(userId);

        //verify
        verify(ratingRepository, times(1)).deleteByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserById_userNotFound() {
        //init
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        //test & verify
        assertThrows(EntityNotFoundException.class, () -> usersService.deleteUserById(userId));
        verify(userRepository, times(1)).existsById(userId);
        verifyNoInteractions(ratingRepository);
    }

    @Test
    void getUserById() {
        //init
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("TestUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        //test
        UserPrivateModel result = usersService.getUserById(userId);

        //verify
        assertNotNull(result);
        assertEquals("TestUser", result.username());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_userNotFound() {
        //init
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //test & verify
        assertThrows(ResponseStatusException.class, () -> usersService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByUsername() {
        //init
        String username = "TestUser";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        //test
        UserPrivateModel result = usersService.getUserByUsername(username);

        //verify
        assertNotNull(result);
        assertEquals("TestUser", result.username());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserByUsername_userNotFound() {
        //init
        String username = "NonexistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //test & verify
        assertThrows(ResponseStatusException.class, () -> usersService.getUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void createUser() {
        //init
        UserCreateRequest request = new UserCreateRequest("test@example.com","TestUser", "password123");
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername(request.username());
        userEntity.setEmail(request.email());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        //test
        UserPrivateModel result = usersService.createUser(request);

        //verify
        assertNotNull(result);
        assertEquals("TestUser", result.username());
        assertEquals("test@example.com", result.email());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void checkPassword() {
        //init
        UserEntity userEntity = new UserEntity();
        userEntity.setPasswordHash(new BCryptPasswordEncoder().encode("password123"));

        //test
        boolean result = usersService.checkPassword(userEntity, "password123");

        //verify
        assertTrue(result);
    }

    @Test
    void checkPassword_failed() {
        //init
        UserEntity userEntity = new UserEntity();
        userEntity.setPasswordHash(new BCryptPasswordEncoder().encode("password123"));

        //test
        boolean result = usersService.checkPassword(userEntity, "password1234");

        //verify
        assertFalse(result);
    }
}