package hu.borkutip.chickenusb;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.android.serial.UsbService;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.Set;

public class MainActivity extends RosActivity {

    UsbRosNode node;
    UsbServiceHelper usbHelper;
    UsbMessageHandlerHelper usbHandler;

    public MainActivity() {
        super("ChickenUSB", "ChickenUSB");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MessageLogger loggerFromRos = new MessageLogger(this, (TextView) findViewById(R.id.outputText));

        node = new UsbRosNode(this, loggerFromRos, loggerFromRos, "outputTopic", "inputTopic");

        setupUsbService(node);

        setupUIForTesting(R.id.inputText, R.id.button);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbHelper.usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(usbHelper.broadcastReceiver);
        unbindService(usbHelper.usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(usbHelper.broadcastReceiver, filter);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
                InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeMainExecutor.execute(node, nodeConfiguration);
    }

    private void setupUsbService(UsbRosNode usbNode) {
        usbHandler = new UsbMessageHandlerHelper(this, usbNode);
        usbHelper = new UsbServiceHelper(this, getIntent(), usbNode, usbHandler);
    }

    private void setupUIForTesting(int inputTextFieldId, int sendButtonFieldId) {
        final EditText editText = (EditText) findViewById(inputTextFieldId);
        Button sendButton = (Button) findViewById(sendButtonFieldId);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("") && node != null) {
                    String data = editText.getText().toString();
                    node.sendToRos(data);
                    node.sendToUsb(data);
                }
            }
        });
    }
}
