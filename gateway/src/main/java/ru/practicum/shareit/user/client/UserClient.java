package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.client.BaseClient;

@Service
public class UserClient extends BaseClient {
    public static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String baseUrl, WebClient.Builder webClientBuilder) {
        super(webClientBuilder.baseUrl(baseUrl + API_PREFIX).build());
    }

    public Mono<ResponseEntity<String>> get() {
        return get("/", null, null);
    }

    public Mono<ResponseEntity<String>> get(String pathVariable) {
        return get(String.format("/%s", pathVariable), null, null);
    }

    public Mono<ResponseEntity<String>> post(String body) {
        return post("/", null, null, body);
    }

    public Mono<ResponseEntity<String>> patch(String pathVariable, String body) {
        return patch(String.format("/%s", pathVariable), null, null, body);
    }

    public Mono<ResponseEntity<String>> delete (String pathVariable) {
        return delete(String.format("/%s", pathVariable), null, null);
    }
}
