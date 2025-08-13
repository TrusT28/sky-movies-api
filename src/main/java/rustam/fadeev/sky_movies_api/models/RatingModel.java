package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.RatingEntity;


/**
 * Data Transfer Object (DTO) representing a rating of movie by a user
 * @param movie the movie's metadata
 * @param user public profile of the user
 * @param rating the rating of movie
 */
public record RatingModel(MovieModel movie, UserPublicModel user, Double rating) {
    public RatingModel(RatingEntity entity) {
        this(new MovieModel(entity.getMovie()), new UserPublicModel(entity.getUser()), entity.getRating());
    }
}

