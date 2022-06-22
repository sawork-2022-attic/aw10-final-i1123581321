package cn.edu.nju.cs.service;

public class MessagePublisher {

    private MessageListener listener = null;

    public void subscribe(MessageListener listener){
        this.listener = listener;
    }

    public void remove(){
        this.listener = null;
    }

    public void publish(WaybillInfo info){
        if (listener != null){
            listener.onNext(info);
        }
    }
}
