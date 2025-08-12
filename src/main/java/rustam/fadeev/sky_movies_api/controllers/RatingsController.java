package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.RatingCreateRequest;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.services.RatingsService;

@RestController
@RequestMapping("/ratings")
public class RatingsController {
    private final RatingsService service;

    public RatingsController(RatingsService service) {
        this.service = service;
    }

    @PostMapping("/")
    public RatingModel createRating(@RequestBody RatingCreateRequest request) {
        return service.createRating(request);
    }
}
