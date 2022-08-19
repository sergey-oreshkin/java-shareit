package ru.practicum.shareit.util;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.database.Booking;
import ru.practicum.shareit.item.database.Item;
import ru.practicum.shareit.user.database.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class JdbcUtil {

    final JdbcTemplate jdbcTemplate;

    public long insertUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("users").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", user.getName())
                .addValue("email", user.getEmail());
        return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    public User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .build();
    }

    public long insertItem(Item item) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("items").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", item.getName())
                .addValue("description", item.getDescription())
                .addValue("available", item.getAvailable())
                .addValue("owner_id", item.getOwner().getId());
        return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }

    public Item mapRowToItem(ResultSet rs, int rowNum) throws SQLException {
        return Item.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .available(rs.getBoolean("available"))
                .owner(User.builder().id(rs.getLong("owner_id")).build())
                .build();
    }

    public long insertBooking(Booking booking) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("bookings").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("start_time", booking.getStartTime())
                .addValue("end_time", booking.getEndTime())
                .addValue("item_id", booking.getItem().getId())
                .addValue("booker_id", booking.getBooker().getId())
                .addValue("status", booking.getStatus());
        return (long) simpleJdbcInsert.executeAndReturnKey(parameters);
    }
}
