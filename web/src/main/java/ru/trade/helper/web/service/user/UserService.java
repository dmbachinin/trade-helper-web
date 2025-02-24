package ru.trade.helper.web.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService{

    private final UserRepository userRepository;

    @Transactional
    public void saveOrUpdate(User entity) {
        userRepository.save(entity);
    }

    @Transactional
    public void update(User entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("Некорректный объект для обновления");
        }
        userRepository.save(entity);
    }

    @Transactional
    public void save(User entity) {
        if (userRepository.findById(entity.getId()).isPresent()) {
            throw new IllegalStateException("Объект уже существует");
        }
        userRepository.save(entity);
    }

    @Transactional
    public void delete(User entity) {
        userRepository.delete(entity);
    }

    public List<User> findAllWithLimit(int limit) {
        return userRepository.findAllByLimit(limit);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
