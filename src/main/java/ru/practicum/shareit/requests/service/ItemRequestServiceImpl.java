package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.OffsetLimitPageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.database.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

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
    public List<ItemRequest> getAll(Long userId, Integer from, Integer size) {
        validateUserId(userId);
        Pageable pageable = OffsetLimitPageable.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return requestRepository.findAllByRequesterIdIsNot(userId, pageable).getContent();
    }

    private void validateUserId(long id) {
        userService.getById(id);
    }
}
