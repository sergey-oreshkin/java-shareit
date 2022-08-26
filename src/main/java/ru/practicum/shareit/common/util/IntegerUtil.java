package ru.practicum.shareit.common.util;

import java.util.Optional;

public class IntegerUtil {
    public static int saveUnboxing(Integer value) {
        return Optional.ofNullable(value).orElse(0);
    }
}
