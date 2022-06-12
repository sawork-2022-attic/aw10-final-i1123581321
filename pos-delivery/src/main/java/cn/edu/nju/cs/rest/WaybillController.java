package cn.edu.nju.cs.rest;

import cn.edu.nju.cs.api.WaybillsApi;
import cn.edu.nju.cs.dto.OrderDto;
import cn.edu.nju.cs.dto.WaybillDto;
import cn.edu.nju.cs.mapper.WaybillMapper;
import cn.edu.nju.cs.model.Waybill;
import cn.edu.nju.cs.repository.WaybillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("api")
public class WaybillController implements WaybillsApi {

    private final WaybillMapper waybillMapper;

    private final WaybillRepository waybillRepository;

    @Autowired
    public WaybillController(WaybillMapper waybillMapper, WaybillRepository waybillRepository) {
        this.waybillMapper = waybillMapper;
        this.waybillRepository = waybillRepository;
    }

    @Override
    public Mono<ResponseEntity<Flux<WaybillDto>>> listWaybills(Optional<String> orderId, ServerWebExchange exchange) {
        if (orderId.isEmpty()) {
            return Mono.just(ResponseEntity.ok(waybillRepository.findAll().map(waybillMapper::toWaybillDto)));
        } else {
            return Mono.just(ResponseEntity.ok(
                    waybillRepository.findAllByOrderIdLike(orderId.get())
                            .map(waybillMapper::toWaybillDto)
            ));
        }
    }

    @Override
    public Mono<ResponseEntity<WaybillDto>> showWaybillById(String waybillId, ServerWebExchange exchange) {
        return waybillRepository.findById(waybillId)
                .map(waybillMapper::toWaybillDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Bean
    public Consumer<OrderDto> generateWaybill(){
        return d -> waybillRepository.save(new Waybill(d.getId())).subscribe();
    }
}
