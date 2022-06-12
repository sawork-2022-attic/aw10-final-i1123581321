package cn.edu.nju.cs.service;

import cn.edu.nju.cs.model.Cart;
import cn.edu.nju.cs.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<Double> checkout(String id);

    Mono<Cart> addItem(String cartId, Item item);

    Flux<Cart> getAllCarts();

    Mono<Cart> getCartById(String cartId);

    Mono<Cart> saveCart(Cart cart);
}
