package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.NotNull;


/**
 * Data Transfer Object (DTO) representing a request to add a rating for a movie by a user.
 * @param movieId the movie's id in db
 * @param userId the user's id in db
 * @param rating the rating of movie
 */
public record RatingCreateRequest(
        @NotNull
        Long movieId,
        @NotNull
        Long userId,
        @NotNull
        Double rating
) {}
