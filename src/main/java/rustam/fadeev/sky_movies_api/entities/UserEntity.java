package rustam.fadeev.sky_movies_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Email
    @NotNull
    private String email;
    @NotNull
    private String username;
    @NotNull
    @JsonIgnore
    private String passwordHash;
}
