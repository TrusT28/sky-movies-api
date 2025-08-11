package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record UserCreateRequest(
        @NotNull @Email String email,
        @NotNull String username,
        @NotNull String password
) {}
