package com.vvit.aammu.lmsvvit.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.vvit.aammu.lmsvvit.HomeActivity;
import com.vvit.aammu.lmsvvit.R;
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

        Intent resultIntent = new Intent(mCtx, HomeActivity.class);
        resultIntent.putExtra(mCtx.getString(R.string.notify_frag),mCtx.getString(R.string.fragment));
        resultIntent.putExtra(mCtx.getString(R.string.employee),employee);
 PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(pendingIntent);

        @SuppressLint("ServiceCast")
        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);
if (mNotifyMgr != null) {
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotifyMgr.notify(++i,mBuilder.build());
        }
    }


}
