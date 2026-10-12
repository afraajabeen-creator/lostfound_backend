package com.campus.lostfound.config;

import com.campus.lostfound.entity.User;
import com.campus.lostfound.enums.Role;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes default application data on server startup.
 * 
 * VIVA TIP:
 * CommandLineRunner runs automatically once the Spring Application Context is loaded.
 * Here we seed the initial Admin account so the system can be administered immediately!
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@campus.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setFullName("Campus Administrator");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setPhoneNumber("1234567890");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setBlocked(false);

            userRepository.save(admin);
            System.out.println(">>> Seeded default Administrator: admin@campus.com / Admin@123 <<<");
        }
    }
}
