package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static ru.practicum.shareit.util.ParamValidator.getValidatedPaginationParameters;
import static ru.practicum.shareit.util.ParamValidator.validateAndGetState;

@RestController
@RequestMapping(path = "/bookings", produces = "application/json")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient client;

    private final ObjectMapper objectMapper;

    @PostMapping
    public Mono<ResponseEntity<String>> create(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @Valid @RequestBody BookingInputDto bookingDto) throws JsonProcessingException {
        return client.post("/", userId, objectMapper.writeValueAsString(bookingDto));
    }

    @GetMapping("{bookingId}")
    public Mono<ResponseEntity<String>> get(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return client.get(String.format("/%d", bookingId), userId, null);
    }

    @PatchMapping("{bookingId}")
    public Mono<ResponseEntity<String>> approve(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestParam @NotNull Boolean approved) {
        return client.patch(String.format("/%d", bookingId), userId, Map.of("approved", approved));
    }

    @GetMapping
    public Mono<ResponseEntity<String>> getAllByBooker(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) {
        Map<String, Object> params = getValidatedPaginationParameters(from, size);
        params.put("state", validateAndGetState(state).name());
        return client.get("/", userId, params);
    }

    @GetMapping("owner")
    public Mono<ResponseEntity<String>> getAllByItemsOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(name = "from", required = false) Integer from,
            @RequestParam(name = "size", required = false) Integer size) {
        Map<String, Object> params = getValidatedPaginationParameters(from, size);
        params.put("state", validateAndGetState(state).name());
        return client.get("/owner", userId, params);
    }
}
