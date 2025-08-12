package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.RatingEntity;

/**
 * Data Transfer Object (DTO) representing the user's rating for some movie.
 * Main usage is a list of such ratings to create list of all ratings for a given user.
 *
 * <p>This model contains the username of the user who rated the movie and the integer rating value.</p>
 *
 *
 * @param movie the movie object for which a user submitted a rating
 * @param rating the integer rating value given to the movie
 */
public record UserRatingsModel(MovieModel movie, Integer rating) {
    public UserRatingsModel(RatingEntity entity) {
        this(new MovieModel(entity.getMovie()), entity.getRating());
    }
}

