package com.example.auction_server.repository;

import com.example.auction_server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    Optional<User> findByUserIdAndPassword(String userId, String password);
    @Modifying
    @Query("UPDATE user u SET u.deleteTime = :deleteTime WHERE u.id = :id")
    void deleteUser(@Param("id") Long id, @Param("deleteTime") LocalDateTime deleteTime);
}
