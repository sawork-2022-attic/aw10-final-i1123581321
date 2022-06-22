package cn.edu.nju.cs.rest;

import cn.edu.nju.cs.dto.OrderDto;
import cn.edu.nju.cs.model.Waybill;
import cn.edu.nju.cs.repository.WaybillRepository;
import cn.edu.nju.cs.service.MessageListener;
import cn.edu.nju.cs.service.MessagePublisher;
import cn.edu.nju.cs.service.WaybillInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@RestController
@RequestMapping("query")
public class QueryController {

    private static final long DELIVERING_DELAY = 180;
    private static final long DELIVERED_DELAY = 30;

    private final WaybillRepository waybillRepository;

    private final TaskScheduler scheduler = new ConcurrentTaskScheduler();
    private final MessagePublisher publisher;

    private final ConnectableFlux<WaybillInfo> messageFLux;

    @Autowired
    public QueryController(WaybillRepository waybillRepository, MessagePublisher publisher) {
        this.waybillRepository = waybillRepository;
        this.publisher = publisher;

        messageFLux = Flux.<WaybillInfo>create(sink -> {
            publisher.subscribe(new MessageListener() {
                @Override
                public void onNext(WaybillInfo info) {
                    sink.next(info);
                }

                @Override
                public void onComplete() {
                    sink.complete();
                }
            });
        }).publish();
        messageFLux.connect();

//        messageFLux.subscribe(info -> {
//            System.out.printf("%s %s %s\n", info.getTime().toString(), info.getId(), info.getState().toString());
//        });
    }

    @GetMapping("waybills")
    public Flux<ServerSentEvent<String>> getMessage() {
        return Flux.from(messageFLux)
                .map(QueryController::buildEvent);
    }


    private static ServerSentEvent<String> buildEvent(WaybillInfo info) {
        return ServerSentEvent.<String>builder()
                .id(UUID.randomUUID().toString())
                .event("Waybill Status Change")
                .data(info.toString())
                .build();
    }

    @Bean
    public Consumer<OrderDto> generateWaybill() {
        return d -> waybillRepository.save(new Waybill(d.getId())).subscribe(waybill -> {
            var id = waybill.getId();
            // publish waybill created
            publisher.publish(new WaybillInfo(waybill.getId(), waybill.getState(), LocalDateTime.now()));


            // start two scheduled task
            scheduler.schedule(() -> waybillRepository.findById(id)
                    .flatMap(wb -> {
                        wb.setState(Waybill.State.DELIVERING);
                        return waybillRepository.save(wb);
                    })
                    .subscribe(wb -> publisher.publish(new WaybillInfo(wb.getId(), wb.getState(), LocalDateTime.now()))), LocalDateTime.now().plusSeconds(DELIVERING_DELAY).atZone(TimeZone.getDefault().toZoneId()).toInstant());

            scheduler.schedule(() -> waybillRepository.findById(id)
                    .flatMap(wb -> {
                        wb.setState(Waybill.State.DELIVERED);
                        return waybillRepository.save(wb);
                    })
                    .subscribe(wb -> publisher.publish(new WaybillInfo(wb.getId(), wb.getState(), LocalDateTime.now()))), LocalDateTime.now().plusSeconds(DELIVERING_DELAY + DELIVERED_DELAY).atZone(TimeZone.getDefault().toZoneId()).toInstant());
        });
    }
}
