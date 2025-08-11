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
}
