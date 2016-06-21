package hu.borkutip.drumdroidrc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {
    private static String TAG = "DrumdroidRC";

    private AcceleroMeter accel;

    MyNode node;

    public MainActivity() {
        super(TAG, TAG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
                InetAddressFactory.newNonLoopback().getHostAddress());
        nodeConfiguration.setMasterUri(getMasterUri());

        node = new MyNode(this, (TextView) findViewById(R.id.outputText), "DrumdroidIn", "DrumdroidOut");

        nodeMainExecutor.execute(node, nodeConfiguration);

        accel = new AcceleroMeter(this, (TextView) findViewById(R.id.outputText), node);

        accel.start();

        final EditText editText = (EditText) findViewById(R.id.inputText);
        Button sendButton = (Button) findViewById(R.id.button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("") && node != null) {
                    String data = editText.getText().toString();
                    node.send(data);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (accel != null) {
            accel.start();
        }
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onSPause");
        super.onPause();
        if (accel != null) {
            accel.stop();
        }
    }
}
