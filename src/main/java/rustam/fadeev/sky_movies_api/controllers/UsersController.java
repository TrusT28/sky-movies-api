package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.services.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService service;

    public UsersController(UsersService service) {
        this.service = service;
    }

    // TODO all of this should be authenticated
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
