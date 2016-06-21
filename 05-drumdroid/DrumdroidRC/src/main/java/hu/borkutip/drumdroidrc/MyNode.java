package hu.borkutip.drumdroidrc;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

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
public class MyNode extends AbstractNodeMain {
    Activity activity;
    Publisher publisher;
    Subscriber subscriber;
    String subscriberTopic;
    String publisherTopic;
    TextView output;

    public MyNode(Activity activity, TextView output, String publisherTopic, String subscriberTopic) {
        this.activity = activity;
        this.publisherTopic = publisherTopic;
        this.subscriberTopic = subscriberTopic;
        this.output = output;
    }

    public void send(java.lang.String message) {
        if (publisher != null) {
            std_msgs.String str = (std_msgs.String)publisher.newMessage();
            str.setData(message);
            publisher.publish(str);
        }
        else {
            Toast.makeText(activity, "publisher is null, " + message + " lost.", Toast.LENGTH_SHORT).show();
        }
    }

    public void receive(java.lang.String message) {
        // Only the original thread that created a view hierarchy can touch its views
        // http://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
        ViewModifier viewModifier = new ViewModifier(output, message);
        activity.runOnUiThread(viewModifier);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("DrumdroidRCNode");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher = connectedNode.newPublisher(publisherTopic, std_msgs.String._TYPE);
        subscriber = connectedNode.newSubscriber(subscriberTopic, std_msgs.String._TYPE);

        subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
            @Override
            public void onNewMessage(std_msgs.String message) {
                receive(message.getData());
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
