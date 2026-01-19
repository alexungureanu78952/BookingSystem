package server.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import server.entity.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public User findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public boolean existsByUsername(String username) {
        return find("username", username).count() > 0;
    }

    public boolean existsByEmail(String email) {
        return find("email", email).count() > 0;
    }
}
