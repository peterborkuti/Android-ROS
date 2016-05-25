package com.ros.android.pubsub.borkuti.peter.ex01pubsub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity {

    MyNode node;

    public MainActivity() {
        super("Example", "Example");
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

        node = new MyNode(this, (TextView) findViewById(R.id.outputText), "outputTopic", "inputTopic");

        nodeMainExecutor.execute(node, nodeConfiguration);

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
}
