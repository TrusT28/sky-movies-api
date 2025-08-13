package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.UserEntity;

/**
 * Data Transfer Object (DTO) representing private data of the user.
 * @param email of the user
 * @param username of the user
 * @param id of the user in the database
 */
public record UserPrivateModel(Long id, String email, String username) {
    public UserPrivateModel(UserEntity userEntity) {
        this(userEntity.getId(), userEntity.getEmail(), userEntity.getUsername());
    }
}

