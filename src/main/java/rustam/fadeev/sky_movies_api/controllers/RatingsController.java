package rustam.fadeev.sky_movies_api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.RatingCreateRequest;
import rustam.fadeev.sky_movies_api.models.RatingDeleteRequest;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.security.RequirePasswordAuth;
import rustam.fadeev.sky_movies_api.security.UserContext;
import rustam.fadeev.sky_movies_api.services.RatingsService;
import rustam.fadeev.sky_movies_api.services.UsersService;

import java.util.Objects;

@RestController
@RequestMapping("/ratings")
public class RatingsController {
    private final RatingsService service;

    public RatingsController(RatingsService service) {
        this.service = service;
    }

    private static final Logger logger = LoggerFactory.getLogger(RatingsController.class);

    @PostMapping("/")
    @RequirePasswordAuth
    public ResponseEntity<RatingModel> createRating(@RequestBody RatingCreateRequest request) {
        UserPrivateModel loggedInUser = UserContext.get();
        if (!Objects.equals(loggedInUser.username(), "admin") && !loggedInUser.id().equals(request.userId())) {
            logger.debug("User failed auth - cannot create another user's rating");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        RatingModel rating = service.createRating(request);
        return ResponseEntity.ok(rating);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequirePasswordAuth
    public void deleteRating(
            @RequestParam Long movieId,
            @RequestParam Long userId
    ) {
        UserPrivateModel loggedInUser = UserContext.get();
        if (!Objects.equals(loggedInUser.username(), "admin") && !loggedInUser.id().equals(userId)) {
            logger.debug("User failed auth - cannot delete another user's rating");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete another user's rating");
        }
        service.deleteRatingById(movieId, userId);
    }
}
