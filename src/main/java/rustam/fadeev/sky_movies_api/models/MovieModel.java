package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.MovieEntity;

import java.sql.Date;

/**
 * Data Transfer Object (DTO) representing a movie and its metadata.
 * @param id the movie's id in the database
 * @param name the movie's name
 * @param releaseDate of the movie
 */
public record MovieModel(Long id, String name, Date releaseDate) {
    public MovieModel(MovieEntity resultModel) {
        this(resultModel.getId(), resultModel.getName(), resultModel.getReleaseDate());
    }
}

