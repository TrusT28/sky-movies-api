package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.NotNull;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;

import java.sql.Date;

public record RatingModel(Long id, String movieName, String userEmail, Integer rating) {
    public RatingModel(RatingEntity resultModel) {
        this(resultModel.getId(), resultModel.getMovieName(), resultModel.getUserEmail(), resultModel.getRating());
    }
}

