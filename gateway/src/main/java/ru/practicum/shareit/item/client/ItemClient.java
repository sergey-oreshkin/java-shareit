package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    public static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String baseUrl, WebClient.Builder webClientBuilder) {
        super(webClientBuilder.baseUrl(baseUrl + API_PREFIX).build());
    }

    public Mono<ResponseEntity<String>> get(String path, Long userId, Map<String, Object> parameters) {
        return super.get(path, userId, parameters);
    }

    public Mono<ResponseEntity<String>> post(String path, Long userId, String body) {
        return super.post(path, userId, null, body);
    }

    public Mono<ResponseEntity<String>> patch(String path, Long userId, String body) {
        return super.patch(path, userId, null, body);
    }
}
