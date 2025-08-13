package rustam.fadeev.sky_movies_api.models;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the movie's total rating from all users.
 * This allows to add more aggregated statistics
 *
 * <p>This model contains the public profile of the user who rated the movie and the integer rating value.</p>
 *
 * @param movie the movie's metadata
 * @param statistics the total arithmetical average (mean) score of the movie
 * @param ratings list of ratings and who made them of this movie
 */
public record DetailedMovieWithRatingModel(MovieModel movie, MovieStatisticsModel statistics, List<MovieRatingsModel> ratings) {}

