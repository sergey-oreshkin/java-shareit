package ru.practicum.shareit.user.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.util.JdbcUtil;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    final UserRepository userRepository;

    final JdbcTemplate jdbcTemplate;

    final JdbcUtil jdbcUtil;

    User user = User.builder()
            .name("test")
            .email("test.mail.ru")
            .build();

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcUtil = new JdbcUtil(jdbcTemplate);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        jdbcUtil.insertUser(user);

        List<User> result = userRepository.findAll();

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), user.getName());
    }

    @Test
    void findById_shouldReturnUserWithGivenId() {
        var id = jdbcUtil.insertUser(user);

        User result = userRepository.findById(id).orElse(null);

        assertNotNull(result);
        assertEquals(result.getName(), user.getName());
    }

    @Test
    void findById_shouldReturnOptionalEmptyWhenUserNotFound() {
        Optional<User> result = userRepository.findById(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldPersistUserIntoDb() {
        userRepository.save(user);

        String sql = "select * from users";
        User result = jdbcTemplate.query(sql, jdbcUtil::mapRowToUser).stream()
                .findFirst().orElse(null);

        assertNotNull(result);
        assertEquals(result.getName(), user.getName());
    }

    @Test
    void save_shouldThrowDataIntegrityViolationExceptionWhenEmailIsNotUnique() {
        userRepository.save(user);
        User newUser = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(newUser));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    void deleteById_shouldDeleteUserFromDb() {
        var id = jdbcUtil.insertUser(user);

        userRepository.deleteById(id);

        String selectSql = "select * from users";
        User result = jdbcTemplate.query(selectSql, jdbcUtil::mapRowToUser).stream()
                .findFirst().orElse(null);

        assertNull(result);
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    void deleteById_shouldThrowEmptyResultDataAccessExceptionWhenUserWithGivenIdNotExist() {
        assertThrows(EmptyResultDataAccessException.class, () -> userRepository.deleteById(99L));
    }
}