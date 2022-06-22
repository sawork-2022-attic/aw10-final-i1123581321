package cn.edu.nju.cs.test;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PosSimulation extends Simulation {
    Random random = new Random();
    HttpProtocolBuilder httpProtocol = http // 4
            .baseUrl("http://localhost:8080")
            .contentTypeHeader("application/json")
            .acceptHeader("*/*");


    // step one
    ChainBuilder browse = exec(http("browse")
            // get product list
            .get("/products/api/products")
            // random page
            .queryParam("page", random.nextInt(10000))
            // random index
            .check(
                    jsonPath("$[3]").saveAs("product"),
                    jsonPath("$[9]").saveAs("product2"),
                    jsonPath("$[3].price").ofDouble().saveAs("price"),
                    jsonPath("$[9].price").ofDouble().saveAs("price2")
            ))
            .pause(2);

    // step two
    ChainBuilder add = exec(http("add to cart")
            // add to cart
            .post("/carts/api/carts")
            .body(StringBody("{\"items\":[{\"amount\": 1, \"product\": #{product}}] }"))
            .check(
                    jsonPath("$.id").saveAs("cartId"),
                    jsonPath("$").saveAs("cart")
            ))
            .pause(2);

    // step three
    ChainBuilder queryCart = exec(http("check cart content")
            // query by id
            .get("/carts/api/carts/#{cartId}")
            .check(
                    jsonPath("$").is(session -> session.get("cart"))
            ))
            .pause(2);

    // step four
    ChainBuilder addItem = exec(http("add item to cart")
            .post("/carts/api/carts/#{cartId}")
            .body(StringBody("{\"amount\": 2, \"product\": #{product2} }"))
            .check(
                    jsonPath("$").saveAs("cart")
            ))
            .pause(2);

    // step five
    ChainBuilder total = exec(http("get total price")
            .get("/carts/api/carts/#{cartId}/total")
            .check(
                    bodyString().is(session ->
                        Double.valueOf(session.getDouble("price") + session.getDouble("price2") * 2).toString()
                    )
            ))
            .pause(2);

    // step six
    ChainBuilder order = exec(http("generate order")
            .post("/orders/api/orders")
            .body(StringBody("#{cart}"))
            .check(
                    jsonPath("$").saveAs("order"),
                    jsonPath("$.id").saveAs("orderId")
            ))
            .pause(5);

    // step seven
    ChainBuilder waybill = exec(http("query waybill")
            .get("/waybills/api/waybills")
            .queryParam("orderId", session -> session.get("orderId"))
            .check(
                    jsonPath("$[0].order_id").ofString().is(session -> session.get("orderId"))
            ))
            .pause(2);

    ScenarioBuilder scn = scenario("BasicSimulation")
            .exec(browse, add, queryCart, addItem, total, order, waybill);

    {
        setUp(
                scn.injectOpen(
                       rampUsers(5000).during(10)
                        // atOnceUsers(2000)
                )

        ).protocols(httpProtocol); // 13
    }
}
