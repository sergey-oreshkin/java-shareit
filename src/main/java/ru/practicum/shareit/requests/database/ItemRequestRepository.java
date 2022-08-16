package ru.practicum.shareit.requests.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    //Optional<ItemRequest> findByIdAndRequesterId(Long requestId, Long requesterId);

    @Query(value = "select * from requests where not requester_id = ?3 order by created desc offset ?1 limit ?2", nativeQuery = true)
    List<ItemRequest> findAll(long from, long size, long requesterId);
}
