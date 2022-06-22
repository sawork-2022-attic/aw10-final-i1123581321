package cn.edu.nju.cs.rest;

import cn.edu.nju.cs.api.OrdersApi;
import cn.edu.nju.cs.dto.CartDto;
import cn.edu.nju.cs.dto.OrderDto;
import cn.edu.nju.cs.mapper.OrderMapper;
import cn.edu.nju.cs.model.Order;
import cn.edu.nju.cs.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class OrderController implements OrdersApi {
    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final StreamBridge streamBridge;

    @Autowired
    public OrderController(OrderRepository orderRepository, OrderMapper orderMapper, StreamBridge streamBridge) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.streamBridge = streamBridge;
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> createOrder(Mono<CartDto> cartDto, ServerWebExchange exchange) {
        return cartDto
                .map(cart -> {
                    double total = cart.getItems().stream()
                            .map(i -> i.getProduct().getPrice() * i.getAmount())
                            .reduce(0.0, Double::sum);
                    return new Order(cart.getItems().stream().map(orderMapper::toItem).collect(Collectors.toList()), total);
                })
                .flatMap(orderRepository::save)
                .map(o -> {
                    var orderDto = orderMapper.toOrderDto(o);
                    streamBridge.send("order", orderDto);
//                    System.out.println("send order to rabbitmq");
                    return orderDto;
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<OrderDto>>> listOrders(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(orderRepository.findAll().map(orderMapper::toOrderDto)));
    }

    @Override
    public Mono<ResponseEntity<OrderDto>> showOrderById(String orderId, ServerWebExchange exchange) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toOrderDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
