package cn.edu.nju.cs.repository;

import cn.edu.nju.cs.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;




public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Flux<Product> findBy(Pageable pageable);

    Flux<Product> findByNameLike(String name, Pageable pageable);
}
