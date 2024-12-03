package edu.ace.infinite.application;

import android.annotation.SuppressLint;
import android.content.Context;

import edu.ace.infinite.utils.PhoneMessage;

public class Application extends android.app.Application {
    @SuppressLint("StaticFieldLeak")
    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        PhoneMessage.initMessage(context);
    }
}
