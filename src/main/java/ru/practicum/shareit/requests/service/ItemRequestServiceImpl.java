package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<ItemRequest> getAll(Integer from, Integer size, Long userId) {
        validateUserId(userId);
        if (from == null && size == null) {
            from = 0;
            size = 500;
        }
        if (saveUnboxing(size) < 1 || saveUnboxing(from) < 0) {
            throw new ValidationException("from must be positive and size must be more then 0");
        }
        LimitOffsetPage pageable = new LimitOffsetPage(saveUnboxing(from), saveUnboxing(size), Sort.by(Sort.Direction.DESC, "created"));
        return requestRepository.findAllByRequesterIdIsNot(userId, pageable).getContent();
    }

    private void validateUserId(long id) {
        userService.getById(id);
    }

    private int saveUnboxing(Integer value) {
        return Optional.ofNullable(value).orElse(0);
    }

    static class LimitOffsetPage implements Pageable {
        private final int offset;
        private final int limit;
        private Sort sort;

        public LimitOffsetPage(int offset, int limit, Sort sort) {
            this.offset = offset;
            this.limit = limit;
            this.sort = sort;
        }

        @Override
        public int getPageNumber() {
            return 0;
        }

        @Override
        public int getPageSize() {
            return limit;
        }

        @Override
        public long getOffset() {
            return offset;
        }

        @Override
        public Sort getSort() {
            return sort;
        }

        @Override
        public Pageable next() {
            return null;
        }

        @Override
        public Pageable previousOrFirst() {
            return null;
        }

        @Override
        public Pageable first() {
            return null;
        }

        @Override
        public Pageable withPage(int pageNumber) {
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }
    }
}
