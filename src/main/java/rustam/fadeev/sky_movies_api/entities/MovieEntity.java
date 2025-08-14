package rustam.fadeev.sky_movies_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
@Table(name = "movies", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"),
})
public class MovieEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(nullable = false, unique = true)
    private String name;
    @NotNull
    private Date releaseDate;

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void setId(Long id) {
        this.id = id;
    }
}