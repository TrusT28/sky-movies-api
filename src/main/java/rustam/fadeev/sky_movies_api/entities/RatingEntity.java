package rustam.fadeev.sky_movies_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
@Table(name = "ratings")
public class RatingEntity {

    @EmbeddedId
    private RatingId id;

    // leverage JPA's functionality for future aggregations
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @MapsId("movieId")
    @JoinColumn(name = "movie_id")
    private MovieEntity movie;

    @Column(nullable = false)
    private Integer rating;


    public UserEntity getUser() {
        return user;
    }

    public MovieEntity getMovie() {
        return movie;
    }

    public Integer getRating() {
        return rating;
    }

    public RatingId getId() {
        return id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setMovie(MovieEntity movie) {
        this.movie = movie;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setId(RatingId id) {
        this.id = id;
    }
}
