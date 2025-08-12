package rustam.fadeev.sky_movies_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
@Table(name = "ratings")
public class RatingEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @NotNull
    private String movieName;
    @NotNull
    private String userEmail;
    @NotNull
    private Integer rating;

    public Long getId() {
        return id;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}
