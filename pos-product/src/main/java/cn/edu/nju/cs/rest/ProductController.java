package cn.edu.nju.cs.rest;

import cn.edu.nju.cs.api.ProductsApi;
import cn.edu.nju.cs.dto.ProductDto;
import cn.edu.nju.cs.mapper.ProductMapper;
import cn.edu.nju.cs.model.Product;
import cn.edu.nju.cs.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("api")
public class ProductController implements ProductsApi {
    private static final int PAGE_SIZE = 10;

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductDto>>> listProducts(Optional<Integer> page, Optional<String> name, ServerWebExchange exchange) {
        var request = PageRequest.of(page.orElse(0), PAGE_SIZE);
        Flux<Product> result;
        if (name.isEmpty()) {
            result = productRepository.findBy(request);
        } else {
            result = productRepository.findByNameLike(name.get(), request);
        }
        return Mono.just(ResponseEntity.ok(result
                .map(productMapper::toProductDto)));
    }

    @Override
    public Mono<ResponseEntity<ProductDto>> showProductById(String productId, ServerWebExchange exchange) {
        return productRepository.findById(productId)
                .map(productMapper::toProductDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
