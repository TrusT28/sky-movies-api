package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RatingCreateRequest(
        @NotNull
        Long movieId,
        @NotNull
        Long userId,
        @NotNull
        Integer rating
) {}
