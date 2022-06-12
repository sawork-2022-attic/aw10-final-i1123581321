package cn.edu.nju.cs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("waybills")
public class Waybill {
    public enum State {
        CREATED, DELIVERING, DELIVERED
    }

    @Id
    private String id;

    private String orderId;

    private State state = State.CREATED;

    public Waybill() {

    }

    public Waybill(String orderId) {
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
