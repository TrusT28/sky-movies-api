package rustam.fadeev.sky_movies_api.models;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the movie and its statistics
 *
 * @param movie the movie's metadata
 * @param statistics the movie's statistics
 */
public record SimpleMovieWithRatingModel(MovieModel movie, MovieStatisticsModel statistics) {}

