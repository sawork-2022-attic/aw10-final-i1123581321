package cn.edu.nju.cs.repository;

import cn.edu.nju.cs.model.Cart;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;


public interface CartRepository extends ReactiveMongoRepository<Cart, String> {

}
