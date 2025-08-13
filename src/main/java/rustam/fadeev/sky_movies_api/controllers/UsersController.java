package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.*;
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

    @GetMapping
    public PageResponse<UserPublicModel> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Page<UserPublicModel> result = service.getAllUsers(page, size);
        return new PageResponse<>(result);
    }

    // TODO all of this should be authenticated
    @GetMapping("/{userId}/ratings")
    public List<UserRatingsModel> getRatingsOfUserById(@PathVariable Long userId) {
        return ratingsService.getRatingsOfUserById(userId);
    }

    @GetMapping("/named/{username}")
    public UserPrivateModel getUserByUsername(@RequestParam String username) {
        return service.getUserByUsername(username);
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
