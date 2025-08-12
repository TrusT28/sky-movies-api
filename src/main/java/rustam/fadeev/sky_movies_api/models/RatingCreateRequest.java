package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RatingCreateRequest(
        @NotNull
        String movieName,
        @NotNull
        @Email
        String userEmail,
        @NotNull
        Integer rating
) {}
