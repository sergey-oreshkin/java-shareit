package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.database.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    private final UserService userService;

    @Override
    public ItemRequest create(ItemRequest itemRequest) {
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getByOwnerId(Long requesterId) {
        validateUserId(requesterId);
        return requestRepository.findAllByRequesterId(requesterId);
    }

    @Override
    public ItemRequest getById(Long requestId, Long requesterId) {
        validateUserId(requesterId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }

    @Override
    public List<ItemRequest> getAll(Long from, Long size, Long userId) {
        if (from == null && size == null) {
            from = 0L;
            size = Long.MAX_VALUE;
        }
        if (saveUnboxing(size) < 1 || saveUnboxing(from) < 0) {
            throw new ValidationException("from must be positive and size must be more then 0");
        }
        return requestRepository.findAll(saveUnboxing(from), saveUnboxing(size), userId);
    }

    private void validateUserId(long id) {
        userService.get(id);
    }

    private long saveUnboxing(Long value) {
        return Optional.ofNullable(value).orElse(0L);
    }
}
