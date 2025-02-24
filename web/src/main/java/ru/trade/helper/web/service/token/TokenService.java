package ru.trade.helper.web.service.token;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trade.helper.web.entity.Token;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.repository.TokenRepository;
import ru.trade.helper.web.service.account.TradeTokenManagerService;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
public class TokenService {

    private final TradeTokenManagerService accountManagerService;
    private final TokenRepository tokenRepository;
    
    @Transactional
    public void saveOrUpdate(Token entity) {
        tokenRepository.save(entity);
        accountManagerService.updateToken(entity);
    }


    @Transactional
    public void update(Token entity) {
        if (entity.getId() == null) {
            throw new IllegalStateException("Некорректный объект для обновления");
        }
        tokenRepository.save(entity);
        accountManagerService.updateToken(entity);
    }


    @Transactional
    public void save(Token entity) {
        if (entity.getId() != null && tokenRepository.findById(entity.getId()).isPresent()) {
            throw new IllegalStateException("Объект уже существует");
        }
        tokenRepository.save(entity);
    }


    @Transactional
    public void delete(Token entity) {
        tokenRepository.delete(entity);
    }

    public List<Token> findAllWithLimit(int limit) {
        return tokenRepository.findAllWithLimit(limit);
    }


    public List<Token> findAllByUserWithLimit(User user, int limit) {
        return tokenRepository.findAllByUserIdWithLimit(user.getId(), limit);
    }


    public List<Token> findAll() {
        return tokenRepository.findAll();
    }


    public long countByUser(User user) {
        return tokenRepository.countByUserId(user.getId());
    }


    public Optional<Token> findByIdAndUser(Long id, User user) {
        return tokenRepository.findByIdAndUserId(id, user.getId());
    }


    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Optional<Token> findById(Long id) {
        return tokenRepository.findById(id);
    }

}
