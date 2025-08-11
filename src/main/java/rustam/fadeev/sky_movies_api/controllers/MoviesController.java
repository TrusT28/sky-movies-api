package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.MovieCreateRequest;
import rustam.fadeev.sky_movies_api.models.MovieModel;
import rustam.fadeev.sky_movies_api.services.MovieService;

@RestController
@RequestMapping("/movies")
public class MoviesController {
    private final MovieService service;

    public MoviesController(MovieService service) {
        this.service = service;
    }

    @GetMapping("/named/{movieName}")
    public MovieModel getMovieByName(@PathVariable String movieName) {
        return service.getMovieByName(movieName);
    }

    @PostMapping("/")
    public MovieModel createMovie(@RequestBody MovieCreateRequest request) {
        return service.createMovie(request);
    }
}
