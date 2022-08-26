package ru.practicum.shareit.booking.database;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.database.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    List<Booking> findAllByItemOwner(User user, Pageable pageable);
}
