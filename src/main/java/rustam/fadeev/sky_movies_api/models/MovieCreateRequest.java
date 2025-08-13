package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.NotNull;

import java.sql.Date;

/**
 * Data Transfer Object (DTO) representing a request to add a movie.
 * @param name the movie's name
 * @param releaseDate of the movie
 */
public record MovieCreateRequest(
        @NotNull String name,
        @NotNull Date releaseDate
) {}
