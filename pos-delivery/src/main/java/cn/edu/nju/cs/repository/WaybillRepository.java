package cn.edu.nju.cs.repository;

import cn.edu.nju.cs.model.Waybill;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface WaybillRepository extends ReactiveMongoRepository<Waybill, String> {
    Flux<Waybill> findAllByOrderIdLike(String orderId);
}
