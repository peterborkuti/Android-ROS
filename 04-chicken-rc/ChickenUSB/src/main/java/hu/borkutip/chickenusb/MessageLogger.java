package hu.borkutip.chickenusb;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by peter on 2016.05.27..
 */
public class MessageLogger {
    Activity activity;
    TextView textView;

    public MessageLogger(Activity activity, TextView textView) {
        this.activity = activity;
        this.textView = textView;
    }

    public void log(String message) {
        // Only the original thread that created a view hierarchy can touch its views
        // http://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi

        TextViewModifierRunnable textViewModifierRunnable = new TextViewModifierRunnable(textView, message);
        activity.runOnUiThread(textViewModifierRunnable);
    }
}

class TextViewModifierRunnable implements Runnable {

    private TextView view;
    private String message;

    TextViewModifierRunnable(TextView view, String message) {
        this.message = message;
        this.view = view;
    }

    @Override
    public void run() {
        view.setText(message);
    }

}
