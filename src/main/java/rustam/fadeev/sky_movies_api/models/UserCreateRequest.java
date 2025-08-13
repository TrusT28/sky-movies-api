package rustam.fadeev.sky_movies_api.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

/**
 * Data Transfer Object (DTO) representing a request to add user
 * @param email of the user
 * @param username of the user
 * @param password of the user
 */
public record UserCreateRequest(
        @NotNull @Email String email,
        @NotNull String username,
        @NotNull String password
) {}
