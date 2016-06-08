package org.ros.android.serial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by peter on 2016.05.26..
 */
public class UsbBroadcastReceiver extends BroadcastReceiver {
    private Context context;
    private Intent intent;

    public UsbBroadcastReceiver(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                break;
            case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                break;
            case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                break;
            case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                break;
            case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
