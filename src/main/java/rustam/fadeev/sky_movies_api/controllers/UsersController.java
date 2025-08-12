package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.MovieRatingsModel;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.models.UserRatingsModel;
import rustam.fadeev.sky_movies_api.services.RatingsService;
import rustam.fadeev.sky_movies_api.services.UsersService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService service;
    private final RatingsService ratingsService;

    public UsersController(UsersService service, RatingsService ratingsService) {
        this.service = service;
        this.ratingsService = ratingsService;
    }

    // TODO all of this should be authenticated
    @GetMapping("/{userId}/ratings")
    public List<UserRatingsModel> getRatingsOfUserById(@PathVariable Long userId) {
        return ratingsService.getRatingsOfUserById(userId);
    }

    @GetMapping
    public UserPrivateModel getUserByUsername(@RequestParam String name) {
        return service.getUserByUsername(name);
    }

    @GetMapping("/{userId}")
    public UserPrivateModel getUserById(@PathVariable Long userId) {
        return service.getUserById(userId);
    }

    @PostMapping("/")
    public UserPrivateModel createUser(@RequestBody UserCreateRequest request) {
        return service.createUser(request);
    }
}
