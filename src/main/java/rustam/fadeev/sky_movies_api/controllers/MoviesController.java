package rustam.fadeev.sky_movies_api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rustam.fadeev.sky_movies_api.models.*;
import rustam.fadeev.sky_movies_api.services.MovieService;
import rustam.fadeev.sky_movies_api.services.RatingsService;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/movies")
public class MoviesController {
    private final MovieService service;
    private final RatingsService ratingsService;

    public MoviesController(MovieService service, RatingsService ratingsService) {
        this.service = service;
        this.ratingsService = ratingsService;
    }

    @GetMapping
    public PageResponse<MovieModel> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        Page<MovieModel> result = service.getAllMovies(page, size);
        return new PageResponse<>(result);
    }

    @GetMapping("/{movieId}")
    public MovieModel getMovieById(@PathVariable Long movieId) {
        return service.getMovieById(movieId);
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteMovieById(@PathVariable Long movieId) {
        service.deleteMovieById(movieId);
    }

    @GetMapping("/{movieId}/ratings")
    public DetailedMovieWithRatingModel getRatingsOfMovieById(@PathVariable Long movieId) {
        return ratingsService.getRatingsOfMovieById(movieId);
    }

    @GetMapping("/top-rated")
    public PageResponse<SimpleMovieWithRatingModel> getTopRatedMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {

        Page<SimpleMovieWithRatingModel> result = ratingsService.getTopRatedMovies(page, size);
        return new PageResponse<>(result);
    }

    @GetMapping("/named/{name}")
    public MovieModel getMovieByName(@RequestParam String name) {
        return service.getMovieByName(name);
    }

    @PostMapping("/")
    public MovieModel createMovie(@RequestBody MovieCreateRequest request) {
        return service.createMovie(request);
    }
}
