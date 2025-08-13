package rustam.fadeev.sky_movies_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;
import rustam.fadeev.sky_movies_api.services.UsersService;

@Component
public class PasswordAuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final UsersService usersService;
    private final AdminConfig adminConfig;

    private static final Logger logger = LoggerFactory.getLogger(PasswordAuthInterceptor.class);

    public PasswordAuthInterceptor(UserRepository userRepository, UsersService usersService, AdminConfig adminConfig) {
        this.userRepository = userRepository;
        this.adminConfig = adminConfig;
        this.usersService = usersService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) return true;
        // Check only endpoints annotated with @RequirePasswordAuth
        if (method.getMethodAnnotation(RequirePasswordAuth.class) == null) return true;

        logger.debug("Starting password auth...");

        String password = request.getHeader("X-Auth-Password");
        String username = request.getHeader("X-Auth-Username");

        if (password == null || username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing X-Auth-Password or X-Auth-Username header");
            return false;
        }

        // Check for admin first - always allow
        if (method.getMethodAnnotation(RequirePasswordAuth.class).onlyForAdmin()) {
            // Allow only if admin
            if (!password.equals(adminConfig.getPassword()) || !username.equals(adminConfig.getUsername())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Admin access required");
                return false;
            }
            return true;
        }
        else {
            // Check for admin first - always allow
            if (password.equals(adminConfig.getPassword()) && username.equals(adminConfig.getUsername())) {
                UserContext.set(new UserPrivateModel(null, null, "admin"));
                return true;
            }
        }

        UserEntity userFromHeader = userRepository.findByUsername(username).orElse(null);
        if (userFromHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid user. User is not found");
            return false;
        }

        if(!usersService.checkPassword(userFromHeader,password)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid password");
            return false;
        }

        UserContext.set(new UserPrivateModel(userFromHeader));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

}
