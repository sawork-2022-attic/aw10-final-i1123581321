package cn.edu.nju.cs.service;

import cn.edu.nju.cs.model.Cart;
import cn.edu.nju.cs.model.Item;
import cn.edu.nju.cs.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    private static Double checkout(Cart cart) {
        double total = 0;
        for (var item :
                cart.getItems()) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }

    @Override
    public Mono<Double> checkout(String id) {
        return cartRepository.findById(id)
                .map(CartServiceImpl::checkout)
                .defaultIfEmpty(Double.NaN);
    }

    @Override
    public Mono<Cart> addItem(String cartId, Item item) {
        return cartRepository.findById(cartId)
                .flatMap(cart -> {
                    cart.add(item);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Flux<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Mono<Cart> getCartById(String cartId) {
        return cartRepository.findById(cartId);
    }

    @Override
    public Mono<Cart> saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
}
