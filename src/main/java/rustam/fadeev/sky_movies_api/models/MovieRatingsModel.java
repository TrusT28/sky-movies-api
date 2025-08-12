package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.RatingEntity;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the movie's rating from some user.
 * Main usage is a list of such ratings to create list of all ratings for a given movie.
 *
 * <p>This model contains the public profile of the user who rated the movie and the integer rating value.</p>
 *
 *
 * @param user the user's public profile who submitted the rating
 * @param rating the integer rating value given by the user
 */
public record MovieRatingsModel(UserPublicModel user, Integer rating) {
    public MovieRatingsModel(RatingEntity entity) {
        this(new UserPublicModel(entity.getUser()), entity.getRating());
    }
}

