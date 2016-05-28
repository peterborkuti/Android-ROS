package com.ros.android.pubsub.borkuti.peter.ex01pubsub;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.ros.android.serial.UsbService;

import java.lang.ref.WeakReference;

/**
 * Created by peter on 2016.05.27..
 * <p/>
 * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
 */
public class UsbMessageHandlerHelper extends Handler {
    private final WeakReference<MainActivity> mActivity;
    private final WeakReference<UsbMessageHandler> msgReceiver;

    public UsbMessageHandlerHelper(MainActivity activity, UsbMessageHandler receiver) {
        mActivity = new WeakReference<>(activity);
        msgReceiver = new WeakReference<>(receiver);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case UsbService.MESSAGE_FROM_SERIAL_PORT:
                String data = (String) msg.obj;
                Toaster.toast(mActivity.get(), "message from usb:" + data, Toast.LENGTH_SHORT);
                msgReceiver.get().receiveFromUsb(data);
                break;
            case UsbService.CTS_CHANGE:
                Toaster.toast(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG);
                break;
            case UsbService.DSR_CHANGE:
                Toaster.toast(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG);
                break;
        }
    }
}
