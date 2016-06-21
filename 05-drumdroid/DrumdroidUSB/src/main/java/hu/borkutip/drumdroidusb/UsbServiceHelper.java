package hu.borkutip.drumdroidusb;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.ros.android.serial.UsbBroadcastReceiver;
import org.ros.android.serial.UsbService;

/**
 * Created by peter on 2016.05.26..
 */
public class UsbServiceHelper {
    public final BroadcastReceiver broadcastReceiver;
    private UsbService usbService;
    private UsbMessageHandlerHelper mHandler;
    private UsbRosNode usbRosNode;

    public final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            usbService = ((UsbService.UsbBinder) iBinder).getService();
            usbService.setHandler(mHandler);
            usbRosNode.setUsbService(usbService);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
            usbRosNode.setUsbService(usbService); //is it superfluous?
        }
    };

    public UsbServiceHelper(Context context, Intent intent, UsbRosNode usbRosNode, UsbMessageHandlerHelper handler) {
        broadcastReceiver = new UsbBroadcastReceiver(context, intent);
        this.usbRosNode = usbRosNode;
        mHandler = handler;
    }

}
