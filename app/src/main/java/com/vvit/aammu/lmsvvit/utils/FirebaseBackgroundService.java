package com.vvit.aammu.lmsvvit.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.HomeActivity;
import com.vvit.aammu.lmsvvit.R;

public class FirebaseBackgroundService extends Service {
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    ;

    //private Firebase f = new Firebase("https://somedemo.firebaseio-demo.com/");
    private ValueEventListener handler;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        handler = new ValueEventListener() {
                       @Override
            public void onDataChange(DataSnapshot arg0) {
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                               postNotif(arg0.getValue().toString());
                           }
                       }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        firebaseDatabase.addValueEventListener(handler);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void postNotif(String notifString) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher_background;
        Notification notification = new Notification(icon, "Firebase" + Math.random(), System.currentTimeMillis());
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Context context = getApplicationContext();
        CharSequence contentTitle = "Background" + Math.random();
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        Notification n  = new Notification.Builder(this)
                .setContentTitle("My message")
                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(new Notification.BigTextStyle().bigText("")).build();
        //  .addAction(R.drawable.line, "", pIntent).build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, n);
    }

}
