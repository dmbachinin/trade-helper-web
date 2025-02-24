package ru.trade.helper.web.service.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.entity.Operation;
import ru.trade.helper.web.repository.OperationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;


    @Transactional
    public void update(Operation operation) {
        if (operation.getId() == null) {
            throw new IllegalStateException("Некорректный объект для обновления");
        }
        operationRepository.save(operation);
    }

    @Transactional
    public void save(Operation operation) {
        // todo переписать метод сохранения операций, чтобы он проверял наличие операции не по id, а по другим параметрам
        if (operationRepository.findById(operation.getId()).isPresent()) {
            throw new IllegalStateException("Объект уже существует");
        }
        operationRepository.save(operation);
    }

    @Transactional
    public void delete(Operation operation) {
        operationRepository.delete(operation);
    }

    public List<Operation> findAllWithLimit(int limit) {
        return operationRepository.findAllWithLimit(limit);
    }

    public List<Operation> findAllByUserWithLimit(User user, int limit) {
        return operationRepository.findAllByUserIdWithLimit(user.getId(), limit);
    }

    public long countByUser(User user) {
        return operationRepository.countByUserId(user.getId());
    }

    public Optional<Operation> findByIdAndUser(String id, User user) {
        return operationRepository.findByIdAndUserId(id, user.getId());
    }

    public Optional<Operation> findById(String id) {
        return operationRepository.findById(id);
    }
}
