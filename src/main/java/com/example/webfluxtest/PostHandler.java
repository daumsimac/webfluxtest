package com.example.webfluxtest;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PostHandler {
    public Mono<ServerResponse> getById(ServerRequest request){
        Long id = Long.parseLong(request.pathVariable("id"));
        String title = request.pathVariable("title");
        String content = request.pathVariable("content");
        Post post = new Post(id, title, content);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(post);
    }

    public Mono<ServerResponse> create(ServerRequest request){
        Mono<MultiValueMap<String, String>> formData = request.formData();

        return formData.flatMap(data -> {
            Map<String, String> dataMap = data.toSingleValueMap();
            String title = dataMap.getOrDefault("title", null);
            String content = dataMap.getOrDefault("content", null);

            Post newPost = new Post(1L, title, content);
            System.out.println(newPost.toString());
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(newPost);
        });
    }

    public Mono<ServerResponse> createFromJson(ServerRequest request){
        Mono<Post> PostMono = request.bodyToMono(Post.class);
        return PostMono.flatMap(post ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(post));
    }
}
