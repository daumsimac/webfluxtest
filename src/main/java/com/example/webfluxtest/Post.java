package com.example.webfluxtest;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Post {
    @Id
    private Long id;
    private String title;
    private String content;
}
