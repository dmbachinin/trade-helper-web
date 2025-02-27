package ru.trade.helper.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.trade.helper.web.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<User> findAllByLimit(@Param("limit") int limit);
}
