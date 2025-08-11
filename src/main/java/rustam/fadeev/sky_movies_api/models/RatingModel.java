package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.MovieEntity;

import java.sql.Date;

public record RatingModel(Long id, String name, Date releaseDate) {
    public RatingModel(MovieEntity resultModel) {
        this(resultModel.getId(), resultModel.getName(), resultModel.getReleaseDate());
    }
}

