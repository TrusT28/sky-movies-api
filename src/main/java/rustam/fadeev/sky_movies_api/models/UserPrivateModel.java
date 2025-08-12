package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.UserEntity;

public record UserPrivateModel(Long id, String email, String username) {
    public UserPrivateModel(UserEntity userEntity) {
        this(userEntity.getId(), userEntity.getEmail(), userEntity.getUsername());
    }
}

