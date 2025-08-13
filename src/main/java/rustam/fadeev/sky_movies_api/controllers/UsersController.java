package rustam.fadeev.sky_movies_api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.models.*;
import rustam.fadeev.sky_movies_api.security.RequirePasswordAuth;
import rustam.fadeev.sky_movies_api.security.UserContext;
import rustam.fadeev.sky_movies_api.services.RatingsService;
import rustam.fadeev.sky_movies_api.services.UsersService;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService service;
    private final RatingsService ratingsService;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    public UsersController(UsersService service, RatingsService ratingsService) {
        this.service = service;
        this.ratingsService = ratingsService;
    }

    @GetMapping
    @RequirePasswordAuth(onlyForAdmin = true)
    public PageResponse<UserPrivateModel> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Page<UserPrivateModel> result = service.getAllUsers(page, size);
        return new PageResponse<>(result);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequirePasswordAuth
    public void deleteUserById(@PathVariable Long userId) {
        UserPrivateModel loggedInUser = UserContext.get();
        if (!Objects.equals(loggedInUser.username(), "admin") && !loggedInUser.id().equals(userId)) {
            logger.debug("User failed auth - cannot delete another user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete another user");
        }
        service.deleteUserById(userId);
    }

    // TODO all of this should be authenticated
    @GetMapping("/{userId}/ratings")
    public List<UserRatingsModel> getRatingsOfUserById(@PathVariable Long userId) {
        return ratingsService.getRatingsOfUserById(userId);
    }

    @GetMapping("/named/{username}")
    @RequirePasswordAuth
    public ResponseEntity<UserPrivateModel> getUserByUsername(@RequestParam String username) {
        UserPrivateModel loggedInUser = UserContext.get();
        if (!Objects.equals(loggedInUser.username(), "admin") && !loggedInUser.username().equals(username)) {
            logger.debug("User failed auth - cannot get info about another user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot get info about another user");
        }
        UserPrivateModel result = service.getUserByUsername(username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    @RequirePasswordAuth
    public ResponseEntity<UserPrivateModel> getUserById(@PathVariable Long userId) {
        UserPrivateModel loggedInUser = UserContext.get();
        if (!Objects.equals(loggedInUser.username(), "admin") && !loggedInUser.id().equals(userId)) {
            logger.debug("User failed auth - cannot get info about another user");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot get info about another user");
        }
        UserPrivateModel result =  service.getUserById(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/")
    public UserPrivateModel createUser(@RequestBody UserCreateRequest request) {
        return service.createUser(request);
    }
}
