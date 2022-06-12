package cn.edu.nju.cs;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class GatewayApplication {
    private static final String PRODUCT = "lb://product-service";
    private static final String CART = "lb://cart-service";
    private static final String ORDER = "lb://order-service";
    private static final String DELIVERY = "lb://delivery-service";

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator route(RouteLocatorBuilder builder){
        return builder.routes()
                .route(p -> p.path("/v3/api-docs/**")
                        .filters(f -> f.rewritePath("/v3/api-docs/(?<path>.*)", "/${path}/v3/api-docs"))
                        .uri("http://localhost:8080"))
                .route(p -> p.path("/products/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(PRODUCT))
                .route(p -> p.path("/carts/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(CART))
                .route(p -> p.path("/orders/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(ORDER))
                .route(p -> p.path("/waybills/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri(DELIVERY))
                .build();
    }
}
