package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.database.ItemRequest;
import ru.practicum.shareit.requests.database.ItemRequestRepository;

@Component
@RequiredArgsConstructor
public class ItemRequestFactory {

    private final ItemRequestRepository requestRepository;

    public ItemRequest getById(Long requestId) {
        if (requestId == null) return null;
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }
}
