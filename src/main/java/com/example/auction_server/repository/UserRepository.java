package com.example.auction_server.repository;

import com.example.auction_server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/***
 * @Repository
 * - Spring 프레임워크에서 데이터 액세스 레이어를 담당하는 클래스에 부여되는 어노테이션
 * - 데이터베이스와의 상호작용을 위한 CRUD(Create, Read, Update, Delete) 기능을 수행하는 클래스를 간단하게 생성
 * - 데이터베이스와의 통신을 처리하는 데 필요한 기능들을 제공하며, 자동으로 인스턴스화되어 Spring의 컨테이너에 등록
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);

    @Query("SELECT u FROM user u WHERE u.id = :id AND u.updateTime IS NULL")
    Optional<User> findByIdWithNullUpdateTime(@Param("id") Long id);

    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndPassword(String userId, String password);

    @Modifying
    @Query("UPDATE user u SET u.updateTime = :updateTime WHERE u.id = :id")
    int withDrawUser(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime);

    boolean existsByEmail(String email);

    boolean existsByUserId(String userId);
}
