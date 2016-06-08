package hu.borkutip.chickenusb;

import android.app.Activity;
import android.widget.Toast;

import org.ros.android.serial.UsbService;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;


/**
 * Created by peter on 2016.05.18..
 */
public class UsbRosNode extends AbstractNodeMain implements UsbMessageHandler {
    private Activity activity;
    private Publisher publisher;
    private Subscriber subscriber;
    private String subscriberTopic;
    private String publisherTopic;
    private UsbService usbService = null;
    private MessageLogger loggerFromUsb;
    private MessageLogger loggerFromRos;

    public UsbRosNode(Activity activity, MessageLogger loggerFromUsb, MessageLogger loggerFromRos,
                      String publisherTopic, String subscriberTopic) {

        this.activity = activity;
        this.publisherTopic = publisherTopic;
        this.subscriberTopic = subscriberTopic;
        this.loggerFromRos = loggerFromRos;
        this.loggerFromUsb = loggerFromUsb;
        this.usbService = usbService;
    }

    // for UsbServiceHelper
    public void setUsbService(UsbService usbService) {
        this.usbService = usbService;
    }

    public void sendToRos(java.lang.String message) {
        if (publisher != null) {
            std_msgs.String str = (std_msgs.String)publisher.newMessage();
            str.setData(message);
            publisher.publish(str);
        }
        else {
            Toaster.toast(activity, "publisher is null, " + message + " lost.", Toast.LENGTH_SHORT);
        }
    }

    public void receiveFromRos(java.lang.String message) {
        loggerFromRos.log(message);
        sendToUsb(message);
    }

    //for MyUsbHandler (UsbMessageHandler interface)
    @Override
    public void receiveFromUsb(String message) {
        loggerFromUsb.log(message);
        sendToRos(message);
    }

    //UsbMessageHandler interface
    @Override
    public void sendToUsb(String message) {
        if (usbService != null) {
            usbService.write((message + "\n").getBytes());
        }
        else {
            Toaster.toast(activity, "usbService is null, " + message + " lost.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ChickenUSB");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(publisherTopic, std_msgs.String._TYPE);
        subscriber = connectedNode.newSubscriber(subscriberTopic, std_msgs.String._TYPE);

        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                receiveFromRos(message.getData());
            }
        });
    }

    @Override
    public void onShutdown(Node node) {
        publisher.shutdown();
        subscriber.shutdown();
    }

    @Override
    public void onShutdownComplete(Node node) {
        publisher = null;
        subscriber = null;
    }
}
