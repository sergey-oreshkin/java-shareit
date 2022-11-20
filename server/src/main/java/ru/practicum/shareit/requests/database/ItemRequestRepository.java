package ru.practicum.shareit.requests.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    Page<ItemRequest> findAllByRequesterIdIsNot(Long requesterId, Pageable pageable);
}
