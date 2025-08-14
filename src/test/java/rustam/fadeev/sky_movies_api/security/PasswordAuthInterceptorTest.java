package rustam.fadeev.sky_movies_api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.method.HandlerMethod;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;
import rustam.fadeev.sky_movies_api.services.UsersService;

import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordAuthInterceptorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UsersService usersService;

    @Mock
    private AdminConfig adminConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    private PasswordAuthInterceptor passwordAuthInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordAuthInterceptor = new PasswordAuthInterceptor(userRepository, usersService, adminConfig);
    }

    @Test
    void preHandle_noRequirePasswordAuthAnnotation() throws Exception {
        //init
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(null);

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertTrue(result);
        verifyNoInteractions(request, response, userRepository, usersService, adminConfig);
    }

    @Test
    void preHandle_missingHeaders() throws Exception {
        //init
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(mock(RequirePasswordAuth.class));
        when(request.getHeader("X-Auth-Password")).thenReturn(null);
        when(request.getHeader("X-Auth-Username")).thenReturn(null);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Missing X-Auth-Password or X-Auth-Username header");
    }

    @Test
    void preHandle_adminAccessRequired_invalidCredentials() throws Exception {
        //init
        RequirePasswordAuth annotation = mock(RequirePasswordAuth.class);
        when(annotation.onlyForAdmin()).thenReturn(true);
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(annotation);
        when(request.getHeader("X-Auth-Password")).thenReturn("wrong-password");
        when(request.getHeader("X-Auth-Username")).thenReturn("wrong-username");
        when(adminConfig.getPassword()).thenReturn("admin-password");
        when(adminConfig.getUsername()).thenReturn("admin-username");
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(writer).write("Admin access required");
    }

    @Test
    void preHandle_adminAccessRequired_validCredentials() throws Exception {
        //init
        RequirePasswordAuth annotation = mock(RequirePasswordAuth.class);
        when(annotation.onlyForAdmin()).thenReturn(true);
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(annotation);
        when(request.getHeader("X-Auth-Password")).thenReturn("admin-password");
        when(request.getHeader("X-Auth-Username")).thenReturn("admin-username");
        when(adminConfig.getPassword()).thenReturn("admin-password");
        when(adminConfig.getUsername()).thenReturn("admin-username");

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertTrue(result);
        verifyNoInteractions(response, userRepository, usersService);
    }

    @Test
    void preHandle_userNotFound() throws Exception {
        //init
        RequirePasswordAuth annotation = mock(RequirePasswordAuth.class);
        when(annotation.onlyForAdmin()).thenReturn(false);
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(annotation);
        when(request.getHeader("X-Auth-Password")).thenReturn("user-password");
        when(request.getHeader("X-Auth-Username")).thenReturn("nonexistent-user");
        when(userRepository.findByUsername("nonexistent-user")).thenReturn(Optional.empty());
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Invalid user. User is not found");
    }

    @Test
    void preHandle_invalidPassword() throws Exception {
        //init
        RequirePasswordAuth annotation = mock(RequirePasswordAuth.class);
        when(annotation.onlyForAdmin()).thenReturn(false);
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(annotation);
        when(request.getHeader("X-Auth-Password")).thenReturn("wrong-password");
        when(request.getHeader("X-Auth-Username")).thenReturn("valid-user");
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByUsername("valid-user")).thenReturn(Optional.of(userEntity));
        when(usersService.checkPassword(userEntity, "wrong-password")).thenReturn(false);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Invalid password");
    }

    @Test
    void preHandle_validUser() throws Exception {
        //init
        RequirePasswordAuth annotation = mock(RequirePasswordAuth.class);
        when(annotation.onlyForAdmin()).thenReturn(false);
        when(handlerMethod.getMethodAnnotation(RequirePasswordAuth.class)).thenReturn(annotation);
        when(request.getHeader("X-Auth-Password")).thenReturn("valid-password");
        when(request.getHeader("X-Auth-Username")).thenReturn("valid-user");
        UserEntity userEntity = new UserEntity();
        when(userRepository.findByUsername("valid-user")).thenReturn(Optional.of(userEntity));
        when(usersService.checkPassword(userEntity, "valid-password")).thenReturn(true);

        //test
        boolean result = passwordAuthInterceptor.preHandle(request, response, handlerMethod);

        //verify
        assertTrue(result);
        verifyNoInteractions(response);
    }
}