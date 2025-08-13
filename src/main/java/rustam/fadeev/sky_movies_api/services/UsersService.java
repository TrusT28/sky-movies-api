package rustam.fadeev.sky_movies_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.MovieEntity;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.MovieModel;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserPrivateModel;
import rustam.fadeev.sky_movies_api.models.UserPublicModel;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

@Service
public class UsersService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public Page<UserPublicModel> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> pageResult = userRepository.findAll(pageable);

        return pageResult.map(UserPublicModel::new);
    }

    public UserPrivateModel getUserById(Long userId) throws ResponseStatusException {
        logger.info("Looking for a movie with name: {}", userId);
        UserEntity result = userRepository.findById(userId).orElseThrow(() -> {
            logger.info("The user with userId: {} is not found", userId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "User with a id" + userId + " not found.");
        });
        logger.info("The user with userId: {} is found", userId);
        return new UserPrivateModel(result);
    }

    public UserPrivateModel getUserByUsername(String username) throws ResponseStatusException {
        logger.info("Looking for a movie with name: {}", username);
        UserEntity result = userRepository.findByUsername(username).orElseThrow(() -> {
            logger.info("The user with username: {} is not found", username);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "User with a username" + username + " not found.");
        });
        logger.info("The user with username: {} is found", username);
        return new UserPrivateModel(result);
    }

    public UserPrivateModel createUser(UserCreateRequest userCreateRequest) {
        logger.info("Creating user with email: {}", userCreateRequest.email());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(userCreateRequest.email());
        userEntity.setUsername(userCreateRequest.username());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userEntity.setPasswordHash(encoder.encode(userCreateRequest.password()));

        UserEntity result = userRepository.save(userEntity);
        logger.info("User {} created with email: {}", result.getUsername(), result.getEmail());
        return new UserPrivateModel(result);
    }
}
