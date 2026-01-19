package server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import server.entity.User;
import server.repository.UserRepository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public User register(String username, String password, String email, String fullName) throws Exception {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new Exception("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new Exception("Email already registered");
        }

        // Hash password
        String passwordHash = hashPassword(password);

        // Create user
        User user = new User(username, passwordHash, email, fullName);
        userRepository.persist(user);

        return user;
    }

    @Transactional
    public User login(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new Exception("Invalid username or password");
        }

        // Verify password
        String passwordHash = hashPassword(password);
        if (!user.passwordHash.equals(passwordHash)) {
            throw new Exception("Invalid username or password");
        }

        return user;
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}
