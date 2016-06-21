package hu.borkutip.drumdroidusb;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by peter on 2016.05.18..
 */
public class Toaster {

    public static void toast(Activity activity, String message, int length) {
        ToasterRunnable toaster = new ToasterRunnable(activity, message, length);
        activity.runOnUiThread(toaster);
    }
}

class ToasterRunnable implements  Runnable {
    private String message;
    private Context context;
    private int length = Toast.LENGTH_SHORT;

    ToasterRunnable(Context context, String message, int length) {
        this.message = message;
        this.context = context;
        this.length = length;
    }

    @Override
    public void run() {
        Toast.makeText(context, "USB Ready", length).show();
    }
}
