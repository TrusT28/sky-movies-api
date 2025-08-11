package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record MovieCreateRequest(
        @NotNull String name,
        @NotNull Date releaseDate
) {}
