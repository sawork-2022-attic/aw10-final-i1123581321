package cn.edu.nju.cs.repository;

import cn.edu.nju.cs.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
}
