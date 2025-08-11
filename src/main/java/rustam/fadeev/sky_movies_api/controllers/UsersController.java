package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserModel;
import rustam.fadeev.sky_movies_api.services.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService service;

    public UsersController(UsersService service) {
        this.service = service;
    }

    // TODO all of this should be authenticated
    @GetMapping("/named/{username}")
    public UserModel getUserByUsername(@PathVariable String username) {
        return service.getUserByUsername(username);
    }

    @PostMapping("/")
    public UserModel createUser(@RequestBody UserCreateRequest request) {
        return service.createUser(request);
    }
}
