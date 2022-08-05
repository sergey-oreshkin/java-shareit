package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartTimeDesc(long userId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartTimeDesc(long userId, BookingStatus status);

    @Query("select b from Booking b where b.booker.id=?1 and b.endTime < current_date order by b.startTime desc")
    List<Booking> findAllByBookerAndPast(long userId);

    @Query("select b from Booking b where b.booker.id=?1 and b.startTime > current_date order by b.startTime desc")
    List<Booking> findAllByBookerAndFuture(long userId);

    @Query("select b from Booking b where b.booker.id=?1 " +
            "and current_date between b.startTime and b.endTime order by b.startTime desc")
    List<Booking> findAllByBookerAndCurrent(long userId);

    List<Booking> findAllByItemInOrderByStartTimeDesc(List<Item> items);

    List<Booking> findAllByItemInAndStatusOrderByStartTimeDesc(List<Item> items, BookingStatus status);

    @Query("select b from Booking b where b.item in (:items) and b.endTime < current_date order by b.startTime desc")
    List<Booking> findAllByItemsAndPast(List<Item> items);

    @Query("select b from Booking b where b.item in (:items) and b.startTime > current_date order by b.startTime desc")
    List<Booking> findAllByItemsAndFuture(List<Item> items);

    @Query("select b from Booking b where b.item in (:items) " +
            "and current_date between b.startTime and b.endTime order by b.startTime desc")
    List<Booking> findAllByItemsAndCurrent(List<Item> items);
}
