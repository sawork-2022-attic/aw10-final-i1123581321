package cn.edu.nju.cs.service;

import cn.edu.nju.cs.model.Waybill;

public interface MessageListener {
    void onNext(WaybillInfo info);

    void onComplete();
}
