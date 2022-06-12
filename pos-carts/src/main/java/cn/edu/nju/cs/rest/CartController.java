package cn.edu.nju.cs.rest;

import cn.edu.nju.cs.api.CartsApi;
import cn.edu.nju.cs.dto.CartDto;
import cn.edu.nju.cs.dto.CartItemDto;
import cn.edu.nju.cs.mapper.CartMapper;
import cn.edu.nju.cs.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api")
public class CartController implements CartsApi {
    private final CartService cartService;
    private final CartMapper cartMapper;

    @Autowired
    public CartController(CartService cartService, CartMapper cartMapper) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
    }

    @Override
    public Mono<ResponseEntity<CartDto>> createCart(Mono<CartDto> cartDto, ServerWebExchange exchange) {
        return cartDto.map(cartMapper::toCart)
                .flatMap(cartService::saveCart)
                .map(cartMapper::toCartDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<CartDto>>> listCarts(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(cartService.getAllCarts().map(cartMapper::toCartDto)));
    }


    @Override
    public Mono<ResponseEntity<CartDto>> addItemToCart(String cartId, Mono<CartItemDto> cartItemDto, ServerWebExchange exchange) {
        return cartItemDto.map(cartMapper::toItem)
                .flatMap(item -> cartService.addItem(cartId, item))
                .map(cartMapper::toCartDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<CartDto>> showCartById(String cartId, ServerWebExchange exchange) {
        return cartService.getCartById(cartId)
                .map(cartMapper::toCartDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Double>> showCartTotal(String cartId, ServerWebExchange exchange) {
        return cartService.checkout(cartId)
                .map(ResponseEntity::ok);
    }
}
