package server.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(nullable = false, unique = true, length = 100)
    public String username;

    @Column(nullable = false, length = 255)
    public String passwordHash;

    @Column(nullable = false, unique = true, length = 255)
    public String email;

    @Column(name = "full_name", nullable = false, length = 255)
    public String fullName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Booking> bookings = new ArrayList<>();

    public User() {
    }

    public User(String username, String passwordHash, String email, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
    }

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
}
