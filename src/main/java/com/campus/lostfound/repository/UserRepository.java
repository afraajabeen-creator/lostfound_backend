package com.campus.lostfound.repository;

import com.campus.lostfound.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * 
 * VIVA TIP:
 * JpaRepository provides out-of-the-box CRUD methods (save, findById, findAll, deleteById)
 * plus pagination and sorting without having to write boilerplate SQL queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
