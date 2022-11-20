package ru.practicum.shareit.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

public class BaseClient {
    public static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    private final WebClient webClient;

    public BaseClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected Mono<ResponseEntity<String>> get(String path, Long userId, Map<String, Object> parameters) {
        return makeAndFetch(HttpMethod.GET, path, userId, mapToMultiValueMap(parameters));
    }

    protected Mono<ResponseEntity<String>> post(String path, Long userId, Map<String, Object> parameters, String body) {
        return makeAndFetch(HttpMethod.POST, path, userId, mapToMultiValueMap(parameters), body);
    }

    protected Mono<ResponseEntity<String>> patch(String path, Long userId, Map<String, Object> parameters, String body) {
        return makeAndFetch(HttpMethod.PATCH, path, userId, mapToMultiValueMap(parameters), body);
    }

    protected Mono<ResponseEntity<String>> patch(String path, Long userId, Map<String, Object> parameters) {
        return makeAndFetch(HttpMethod.PATCH, path, userId, mapToMultiValueMap(parameters));
    }

    protected Mono<ResponseEntity<String>> delete(String path, Long userId, Map<String, Object> parameters) {
        return makeAndFetch(HttpMethod.DELETE, path, userId, mapToMultiValueMap(parameters));
    }

    private Mono<ResponseEntity<String>> makeAndFetch(HttpMethod method, String path, Long userId,
                                                      MultiValueMap<String, String> parameters, String body) {
        return webClient
                .method(method)
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(parameters).build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER_NAME, String.valueOf(userId))
                .body(Mono.just(body), String.class)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> Mono.just(ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ex.getResponseBodyAsString()))
                );
    }

    private Mono<ResponseEntity<String>> makeAndFetch(HttpMethod method, String path, Long userId,
                                                      MultiValueMap<String, String> parameters) {
        return webClient
                .method(method)
                .uri(uriBuilder -> uriBuilder.path(path).queryParams(parameters).build())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_ID_HEADER_NAME, String.valueOf(userId))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> Mono.just(ResponseEntity
                                .status(ex.getStatusCode())
                                .body(ex.getResponseBodyAsString()))
                );
    }

    private MultiValueMap<String, String> mapToMultiValueMap(Map<String, Object> map) {
        if (map == null) {
            return new LinkedMultiValueMap<>();
        }
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        map.forEach((k, v) -> result.put(k, Arrays.asList(v.toString())));
        return result;
    }

}
