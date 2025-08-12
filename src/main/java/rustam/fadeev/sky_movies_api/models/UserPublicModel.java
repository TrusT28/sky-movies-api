package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.UserEntity;

public record UserPublicModel(String username) {
    public UserPublicModel(UserEntity userEntity) {
        this(userEntity.getUsername());
    }
}

