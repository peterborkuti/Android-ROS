package hu.borkutip.drumdroidusb;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        super("DrumdroidUSB", "DrumdroidUSB");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MessageLogger loggerFromRos = new MessageLogger(this, (TextView) findViewById(R.id.outputText));

        node = new UsbRosNode(this, loggerFromRos, loggerFromRos, "DrumdroidOut", "DrumdroidIn");

        setupUsbService(node);

        setupUIForTesting(R.id.inputText, R.id.button);

        setupUIForDrumdroid();
    }

    private int getInteger(int id, int minVal, int maxVal) {
        final EditText editText = (EditText) findViewById(id);
        String text = editText.getText().toString();
        int val = minVal;
        try {
            val = Integer.getInteger(text);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Value must be an integer", Toast.LENGTH_LONG);
        }
        if (val < minVal || val > maxVal) {
            Toast.makeText(this, "Value must be between " + minVal + " and " + maxVal, Toast.LENGTH_LONG);
            val = minVal;
        }

        return val;

    }

    private void setupUIForDrumdroid() {
        Button readSetupButton = (Button) findViewById(R.id.readSetup);

        readSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                node.sendToUsb("X");
            }
        });

        Button setupButton = (Button) findViewById(R.id.setupButton);

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lMin = getInteger(R.id.lMin, 0, 180);
                int lMax = getInteger(R.id.lMax, 0, 180);
                int lDly = getInteger(R.id.lDelay, 0, 1000);

                int rMin = getInteger(R.id.rMin, 0, 180);
                int rMax = getInteger(R.id.rMax, 0, 180);
                int rDly = getInteger(R.id.rDelay, 0, 1000);

                /*
                node.sendToUsb("LR" + lMin);
                node.sendToUsb("LH" + lMax);
                node.sendToUsb("RR" + rMin);
                node.sendToUsb("RH" + rMax);
                node.sendToUsb("LD" + lDly);
                node.sendToUsb("RD" + rDly);
                */
            }
        });

        Button leftStick = (Button) findViewById(R.id.leftStick);

        leftStick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                node.sendToUsb("L");
            }
        });
        Button rightStick = (Button) findViewById(R.id.rightStick);

        rightStick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                node.sendToUsb("R");
            }
        });
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
