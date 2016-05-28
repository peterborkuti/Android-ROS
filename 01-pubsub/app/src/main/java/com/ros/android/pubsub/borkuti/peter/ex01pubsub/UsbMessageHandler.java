package com.ros.android.pubsub.borkuti.peter.ex01pubsub;

/**
 * Created by peter on 2016.05.27..
 */
public interface UsbMessageHandler {
    public void receiveFromUsb(String message);
    public void sendToUsb(String message);
}
