package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QueryByExampleExecutor<Item> {
    List<Item> findItemsByOwner(User user);
}









