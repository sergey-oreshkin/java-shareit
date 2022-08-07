package ru.practicum.shareit.item.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import ru.practicum.shareit.user.database.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QueryByExampleExecutor<Item> {
    List<Item> findAllByOwner(User user);

    List<Item> findAllByOwner_Id(long id);
}









