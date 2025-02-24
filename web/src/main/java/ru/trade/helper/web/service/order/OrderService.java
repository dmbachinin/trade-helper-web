package ru.trade.helper.web.service.order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.entity.Order;
import ru.trade.helper.web.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    
    @Transactional
    public void update(Order order) {
        if (order.getId() == null) {
            throw new IllegalStateException("Некорректный объект для обновления");
        }
        orderRepository.save(order);
    }
    
    @Transactional
    public void save(Order order) {
        if (orderRepository.findById(order.getId()).isPresent()) {
            throw new IllegalStateException("Объект уже существует");
        }
        orderRepository.save(order);
    }
    
    @Transactional
    public void delete(Order order) {
        orderRepository.delete(order);
    }
    
    public List<Order> findAllWithLimit(int limit) {
        return orderRepository.findAllWithLimit(limit);
    }
    
    public List<Order> findAllByUserWithLimit(User user, int limit) {
        return orderRepository.findAllByUserIdWithLimit(user.getId(), limit);
    }
    
    public long countByUser(User user) {
        return orderRepository.countByUserId(user.getId());
    }
    
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> findByIdAndUser(String id, User user) {
        return orderRepository.findByIdAndUserId(id, user.getId());
    }

}
