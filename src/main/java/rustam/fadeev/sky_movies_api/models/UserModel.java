package rustam.fadeev.sky_movies_api.models;

import rustam.fadeev.sky_movies_api.entities.UserEntity;

public record UserModel(String email, String username) {
    public UserModel(UserEntity userEntity) {
        this(userEntity.getEmail(), userEntity.getUsername());
    }
}

