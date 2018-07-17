package com.vvit.aammu.lmsvvit.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.vvit.aammu.lmsvvit.HomeActivity;
import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.WelcomeActivity;
import com.vvit.aammu.lmsvvit.model.Employee;

import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotificationManager {
    private Context mCtx;
    private static Employee employee;
    static int i;
    private static MyNotificationManager mInstance;

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context, Employee emp) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
            employee = emp;
        }
        return mInstance;
    }

    public void displayNotification(String title, String body) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx,"1")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(body);


        /*
         *  Clicking on the notification will take us to this intent
         *  Right now we are using the MainActivity as this is the only activity we have in our application
         *  But for your project you can customize it as you want
         * */

        Intent resultIntent = new Intent(mCtx, HomeActivity.class);
        resultIntent.putExtra(mCtx.getString(R.string.notify_frag),mCtx.getString(R.string.fragment));
        resultIntent.putExtra(mCtx.getString(R.string.employee),employee);

        /*
         *  Now we will create a pending intent
         *  The method getActivity is taking 4 parameters
         *  All paramters are describing themselves
         *  0 is the request code (the second parameter)
         *  We can detect this code in the activity that will open by this we can get
         *  Which notification opened the activity
         * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
         *  Setting the pending intent to notification builder
         * */

        mBuilder.setContentIntent(pendingIntent);

        @SuppressLint("ServiceCast")
        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        /*
         * The first parameter is the notification id
         * better don't give a literal here (right now we are giving a int literal)
         * because using this id we can modify it later
         * */
        if (mNotifyMgr != null) {
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotifyMgr.notify(++i,mBuilder.build());
        }
    }


}
