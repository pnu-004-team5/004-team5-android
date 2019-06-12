package team5.class004.android.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("aaaaaaaaaa", "-----------");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(GlobalApp.getInstance(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

//        Notification newMessageNotification = new Notification.Builder(context, "habit")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("asd")
//                .setContentText("bbbbbb")
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setVisibility(Notification.VISIBILITY_PUBLIC)
////                .addAction(R.mipmap.ic_launcher, "asd", pendingIntent)
//                .setFullScreenIntent(pendingIntent, true)
//                .build();
//
//// Issue the notification.
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GlobalApp.getInstance());
//        notificationManager.notify(new Random().nextInt(), newMessageNotification);


//        Notification notification = new NotificationCompat.Builder(this, "channel01")
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle("Test")
//                .setContentText("You see me!")
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)   // heads-up
//                .build();
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(0, notification);

//        Notification notification = new NotificationCompat.Builder(GlobalApp.getInstance(), "habit")
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle("Test")
//                .setContentText("You see me!")
//                .setContentIntent(pendingIntent)
//                .addAction(R.mipmap.ic_launcher, "asd", pendingIntent)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_MAX)   // heads-up
//                .build();
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(GlobalApp.getInstance());
//        notificationManager.notify(0, notification);




//        NotificationCompat.Builder builder = new NotificationCompat.Builder(GlobalApp.getInstance(), "habit")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setFullScreenIntent(pendingIntent, true)
//                .setOngoing(true)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setVibrate(new long[] {1000})
//                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setContentIntent(pendingIntent)
//                .addAction(R.mipmap.ic_launcher, "asd", pendingIntent);
//
//        NotificationManager notificationManager = GlobalApp.getInstance().getSystemService(NotificationManager.class);
//        notificationManager.notify(9, builder.build());




        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "habit")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(GlobalApp.getInstance().prefs.getString("notification_habit_name", "_"))
                .setContentText("습관을 달성할 시간입니다.")
                .setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);
        builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        builder.setSound(defaultSoundUri);

        getManager(context).notify(new Random().nextInt(9999), builder.build());
    }

    private static android.app.NotificationManager getManager(Context context) {
        return (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
