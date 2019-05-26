package com.example.Lab4;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.Calendar;
import android.app.Notification;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import android.content.Context;

import static com.example.Lab4.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;


    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);

        calendar = (CalendarView) findViewById(R.id.calendar1);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayofMonth) {

                Calendar date1 = new GregorianCalendar(year, month, dayofMonth);
                Calendar date2 = GregorianCalendar.getInstance();
                long msDiff = date1.getTimeInMillis() - date2.getTimeInMillis();
                long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)+1;
                if (msDiff > 0) {
                    //date1.set(Calendar.DAY_OF_MONTH, date2.get(Calendar.DAY_OF_MONTH));
                    date1.set(Calendar.HOUR_OF_DAY, 9);
                    //date1.set(Calendar.MINUTE, date2.get(Calendar.MINUTE));
                    //date1.set(Calendar.SECOND, date2.get(Calendar.SECOND) + 60);
                    //date2.set(Calendar.SECOND, 0);
                    long nmsDiff = date1.getTimeInMillis() - date2.getTimeInMillis();
                    //nmsDiff = nmsDiff + 1*60*100000;
                    long timeInMilliseconds = SystemClock.elapsedRealtime() + nmsDiff;
                    //long timeInMilliseconds = SystemClock.elapsedRealtime() + 100*1000;

                    Toast.makeText(getBaseContext(), daysDiff + " days left", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getBaseContext(), android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss a", date1.getTime()) + " days left", Toast.LENGTH_LONG).show();

                    sendOnChannel1(view, timeInMilliseconds);
                }
            }
        });

    }
    public void sendOnChannel1(View v, long time) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Уведомление")
                .setContentText("Событие " + android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()) + " наступило")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(time)
                .build();
        scheduleNotification(notification, time);
        //notificationManager.notify(1, notification);
    }

    private void scheduleNotification(Notification notification, long futureInMillis) {
// https://gist.github.com/BrandonSmith/6679223
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}