package com.tetris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TetrisMultiplayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TetrisMultiplayerApplication.class, args);
    }
}
