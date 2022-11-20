package ru.practicum.shareit.util;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@Component
public class ParamValidator {
    public static Map<String, Object> getValidatedPaginationParameters(Integer from, Integer size) {
        Map<String, Object> parameters = new HashMap<>();
        if (from != null) {
            parameters.put("from", from);
        }
        if (size != null) {
            parameters.put("size", size);
        }
        return parameters;
    }

    public static State validateAndGetState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }
}
