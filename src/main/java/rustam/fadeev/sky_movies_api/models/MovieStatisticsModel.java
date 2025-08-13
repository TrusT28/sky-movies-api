package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.MovieEntity;

import java.sql.Date;
/**
 * Data Transfer Object (DTO) representing the movie's total statistics regarding its rating.
 * Expected to grow with more data.
 *
 * @param average_score the total arithmetical average (mean) score of the movie
 */
public record MovieStatisticsModel(double average_score) {}

