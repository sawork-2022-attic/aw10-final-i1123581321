package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class Cli {

    private int counter = 0;
    private final WebClient client;

    ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {
    };

    private final List<Disposable> connections = new ArrayList<>();

    @Autowired
    public Cli(WebClient client) {
        this.client = client;
    }

    @ShellMethod(value = "test", key = "g")
    public String get(String path){
        var connection = client
                .get()
                .uri("/" + path)
                .retrieve()
                .bodyToFlux(type)
                .subscribe(l -> System.out.printf("request %d event: %s id: %s data: %s%n", counter, l.event(), l.id(), l.data()));
        connections.add(connection);
        counter += 1;
        return String.format("request %d ok", counter);
    }

    @ShellMethod(value = "stop connection", key = "s")
    public String stop(){
        connections.forEach(Disposable::dispose);
        connections.clear();
        return "stop connections";
    }
}
