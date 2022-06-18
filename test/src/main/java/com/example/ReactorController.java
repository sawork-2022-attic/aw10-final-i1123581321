package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("")
public class ReactorController {
    private int counter = 0;

    @Autowired
    public ReactorController( ConnectableFlux<Long> s) {
        this.s = s;
        s
                .doOnSubscribe(a -> System.out.println("a new subscription"))
                .doOnCancel(() -> System.out.println("someone cancel"))
                .doOnComplete(() -> System.out.println("source completed"))
                .subscribe(System.out::println);
        s.connect();
    }
    private final ConnectableFlux<Long> s;

    @GetMapping(value = "odd")
    public Flux<ServerSentEvent<String>> getOdd() {
        return Flux.from(s)
                .filter(l -> l % 2 == 1)
                .map(v -> String.format("value: %d", v))
                .map(v -> ServerSentEvent.<String>builder()
                        .id("random id")
                        .event("odd number")
                        .data(v)
                        .build());
    }

    @GetMapping(value = "even")
    public Flux<ServerSentEvent<String>> getEven() {
        return Flux.from(s)
                .filter(l -> l % 2 == 0)
                .map(v -> String.format("value: %d", v))
                .map(v -> ServerSentEvent.<String>builder()
                        .id("random id")
                        .event("even number")
                        .data(v)
                        .build());
    }
}
