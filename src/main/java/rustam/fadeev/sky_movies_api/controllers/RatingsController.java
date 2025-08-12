package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.RatingCreateRequest;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserModel;
import rustam.fadeev.sky_movies_api.services.RatingsService;
import rustam.fadeev.sky_movies_api.services.UsersService;

@RestController
@RequestMapping("/ratings")
public class RatingsController {
    private final RatingsService service;

    public RatingsController(RatingsService service) {
        this.service = service;
    }

    // TODO all of this should be authenticated
    // TODO get all ratings for the movie - free
    // TODO get all ratings for of the user - auth
    @GetMapping("/named/{id}")
    public RatingModel getUserByUsername(@PathVariable Long id) {
        return service.getRatingById(id);
    }

    @PostMapping("/")
    public RatingModel createUser(@RequestBody RatingCreateRequest request) {
        return service.createRating(request);
    }
}
