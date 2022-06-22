package cn.edu.nju.cs.service;

import cn.edu.nju.cs.model.Waybill;

import java.time.LocalDateTime;

public class WaybillInfo {
    private String id;
    private Waybill.State state;
    private LocalDateTime time;

    public WaybillInfo(String id, Waybill.State state, LocalDateTime time) {
        this.id = id;
        this.state = state;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Waybill.State getState() {
        return state;
    }

    public void setState(Waybill.State state) {
        this.state = state;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "WaybillInfo{" +
                "id='" + id + '\'' +
                ", state=" + state +
                ", time=" + time +
                '}';
    }
}
