package com.ros.android.pubsub.borkuti.peter.ex01pubsub;

import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by peter on 2016.05.18..
 */
public class ViewModifier implements Runnable {

    private TextView view;
    private String message;

    ViewModifier(TextView view, String message) {
        this.message = message;
        this.view = view;
    }

    @Override
    public void run() {
        view.setText(message);
    }

}
