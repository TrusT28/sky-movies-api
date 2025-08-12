package rustam.fadeev.sky_movies_api.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RatingId implements Serializable {

    private Long userId;
    private Long movieId;

    public RatingId() {}

    public RatingId(Long userId, Long movieId) {
        this.userId = userId;
        this.movieId = movieId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingId otherRatingId)) return false;
        return Objects.equals(userId, otherRatingId.userId) &&
                Objects.equals(movieId, otherRatingId.movieId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, movieId);
    }
}

