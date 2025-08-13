package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.NotNull;


/**
 * Data Transfer Object (DTO) representing a request to delete a rating for a movie by a user.
 * @param movieId the movie's id in db
 * @param userId the user's id in db
 */
public record RatingDeleteRequest(
        @NotNull
        Long movieId,
        @NotNull
        Long userId
) {}
