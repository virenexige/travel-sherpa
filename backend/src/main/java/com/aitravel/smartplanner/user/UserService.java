package com.aitravel.smartplanner.user;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final AppUserRepository users;

    public UserService(AppUserRepository users) {
        this.users = users;
    }

    @Transactional
    public AppUser findOrCreate(String email, String name) {
        return users.findByEmail(email)
            .orElseGet(() -> users.save(new AppUser(UUID.randomUUID(), name, email, Instant.now())));
    }
}
