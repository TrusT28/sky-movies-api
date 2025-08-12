package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.RatingEntity;

public record RatingModel(MovieModel movie, UserPublicModel user, Integer rating) {
    public RatingModel(RatingEntity entity) {
        this(new MovieModel(entity.getMovie()), new UserPublicModel(entity.getUser()), entity.getRating());
    }
}

