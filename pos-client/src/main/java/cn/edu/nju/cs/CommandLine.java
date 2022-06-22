package cn.edu.nju.cs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;

import java.time.LocalDateTime;
import java.util.*;

@ShellComponent
public class CommandLine {
    private final WebClient client;

    ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<>() {
    };

    private final Map<UUID, Disposable> connections = new HashMap<>();

    @Autowired
    public CommandLine(WebClient client) {
        this.client = client;
    }


    @ShellMethod(value = "list all waybills", key = "list")
    public String list(){
        return client
                .get()
                .uri("/waybills/api/waybills")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


    @ShellMethod(value = "connect to server", key = "conn")
    public String connect(){
        var uuid = UUID.randomUUID();
        var connection = client
                .get()
                .uri("/waybills/query/waybills")
                .retrieve()
                .bodyToFlux(type)
                .subscribe(event -> System.out.printf("connection-%s: %s#%s: %s\n", uuid, event.event(), event.id(), event.data()));
        connections.put(uuid, connection);
        return String.format("%s connection established", LocalDateTime.now());
    }

    @ShellMethod(value = "disconnect", key = "dis")
    public String disconnect(){
        int number = connections.size();
        connections.forEach((uuid, disposable) -> disposable.dispose());
        connections.clear();
        return String.format("close %d connections", number);
    }
}
