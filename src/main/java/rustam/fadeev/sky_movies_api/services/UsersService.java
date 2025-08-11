package rustam.fadeev.sky_movies_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserModel;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

import java.util.Optional;

@Service
public class UsersService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserModel getUserByUsername(String username) throws ResponseStatusException {
        logger.info("Looking for a movie with name: {}", username);
        Optional<UserEntity> result = userRepository.findByUsername(username);
        if (result.isPresent()) {
            logger.info("The user with username: {} is found", username);
            return new UserModel(result.get());
        }
        else {
            logger.info("The user with username: {} is not found", username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with a username" + username + " not found.");
        }
    }

    public UserModel createUser(UserCreateRequest userCreateRequest) {
        logger.info("Creating user with email: {}", userCreateRequest.email());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userCreateRequest.email());
        userEntity.setUsername(userCreateRequest.username());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userEntity.setPasswordHash(encoder.encode(userCreateRequest.password()));

        UserEntity result = userRepository.save(userEntity);
        logger.info("User {} created with email: {}", result.getUsername(), result.getEmail());
        return new UserModel(result);
    }
}
