package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.NotNull;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;

import java.sql.Date;

public record MovieModel(Long id, String name, Date releaseDate) {
    public MovieModel(MovieEntity resultModel) {
        this(resultModel.getId(), resultModel.getName(), resultModel.getReleaseDate());
    }
}

