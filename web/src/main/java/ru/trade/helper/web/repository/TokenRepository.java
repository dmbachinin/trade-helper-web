package ru.trade.helper.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trade.helper.web.entity.Token;
import ru.trade.helper.web.entity.User;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query(value = "SELECT * FROM tokens " +
            "WHERE user_id = :user_id " +
            "ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Token> findAllByUserIdWithLimit(@Param("user_id") Long userId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM tokens " +
            "ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Token> findAllWithLimit(@Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM tokens WHERE user_id = :user_id", nativeQuery = true)
    long countByUserId(@Param("user_id") Long userId);

    Optional<Token> findByIdAndUserId(Long id, Long userId);

    Optional<Token> findByToken(String token);
}
