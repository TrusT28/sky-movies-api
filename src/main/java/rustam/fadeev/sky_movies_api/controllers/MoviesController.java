package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.MovieCreateRequest;
import rustam.fadeev.sky_movies_api.models.MovieModel;
import rustam.fadeev.sky_movies_api.models.MovieRatingsModel;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.services.MovieService;
import rustam.fadeev.sky_movies_api.services.RatingsService;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MoviesController {
    private final MovieService service;
    private final RatingsService ratingsService;

    public MoviesController(MovieService service, RatingsService ratingsService) {
        this.service = service;
        this.ratingsService = ratingsService;
    }

    @GetMapping("/{movieId}")
    public MovieModel getMovieByName(@PathVariable Long movieId) {
        return service.getMovieById(movieId);
    }

    @GetMapping("/{movieId}/ratings")
    public List<MovieRatingsModel> getRatingsOfMovieById(@PathVariable Long movieId) {
        return ratingsService.getRatingsOfMovieById(movieId);
    }

    @GetMapping
    public MovieModel getMovieByName(@RequestParam String name) {
        return service.getMovieByName(name);
    }

    @PostMapping("/")
    public MovieModel createMovie(@RequestBody MovieCreateRequest request) {
        return service.createMovie(request);
    }
}
