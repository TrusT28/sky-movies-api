package rustam.fadeev.sky_movies_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rustam.fadeev.sky_movies_api.entities.RatingEntity;
import rustam.fadeev.sky_movies_api.entities.UserEntity;
import rustam.fadeev.sky_movies_api.models.RatingCreateRequest;
import rustam.fadeev.sky_movies_api.models.RatingModel;
import rustam.fadeev.sky_movies_api.models.UserCreateRequest;
import rustam.fadeev.sky_movies_api.models.UserModel;
import rustam.fadeev.sky_movies_api.repositories.RatingRepository;
import rustam.fadeev.sky_movies_api.repositories.UserRepository;

import java.util.Optional;

@Service
public class RatingsService {
    private final RatingRepository ratingRepository;
    private static final Logger logger = LoggerFactory.getLogger(RatingsService.class);
    public RatingsService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public RatingModel getRatingById(Long id) throws ResponseStatusException {
        logger.info("Looking for a rating with id: {}", id);
        Optional<RatingEntity> result = ratingRepository.findById(id);
        if (result.isPresent()) {
            logger.info("The rating with id: {} is found", id);
            return new RatingModel(result.get());
        }
        else {
            logger.info("The rating with id: {} is not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "rating with a id" + id + " not found.");
        }
    }

    public RatingModel createRating(RatingCreateRequest ratingCreateRequest) {
        logger.info("Creating a new rating {} from user with email: {} for a movie: {}", ratingCreateRequest.rating(), ratingCreateRequest.userEmail(), ratingCreateRequest.movieName());
        RatingEntity ratingEntity = new RatingEntity();
        // TODO first validate if movie exists
        // TODO validate rating correctness
        ratingEntity.setUserEmail(ratingCreateRequest.userEmail());
        ratingEntity.setMovieName(ratingCreateRequest.movieName());
        ratingEntity.setRating(ratingCreateRequest.rating());

        RatingEntity result = ratingRepository.save(ratingEntity);
        logger.info("Rating was created with id: {}", result.getId());
        return new RatingModel(result);
    }
}
