package ru.trade.helper.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trade.helper.web.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query(value = "SELECT * FROM order_history " +
            "ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Order> findAllWithLimit(@Param("limit") int limit);

    @Query(value = "SELECT * FROM order_history " +
            "WHERE user_id = :user_id " +
            "ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Order> findAllByUserIdWithLimit(@Param("user_id") Long userId, @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM order_history WHERE user_id = :user_id", nativeQuery = true)
    long countByUserId(@Param("user_id") Long userId);

    Optional<Order> findByIdAndUserId(String id, Long userId);
}
