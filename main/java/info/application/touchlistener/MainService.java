package info.application.touchlistener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

public class MainService extends android.app.Service {
    NotificationManager notificationManager;
    private static final String START_FROM_NOTIFICATION = "StartFromNotif";
    private static String LOG_TAG = "XXX";
    int counter = 0;
    NotificationCompat.Builder notificationBuilder;
    private static final String CHANNEL_ID = "1111";
    private FileBuilder fileBuilder;
    private String filename;
    enum STATE{
        IDLE,RECORDING,SAVED;
    }
    private STATE serviceState = STATE.IDLE;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if(bundle != null && bundle.get(START_FROM_NOTIFICATION) != null) {
            if(fileBuilder == null){
                fileBuilder = new FileBuilder();
                filename = FileManager.generateFileName();
                someTask();
                serviceState = STATE.RECORDING;
            }else{
                fileBuilder.SaveToFile(filename,this);
                fileBuilder = null;
                serviceState = STATE.SAVED;
            }
        }else {
            //String channel_id = createNotificationChannel(context);
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }
        sendNotif();
        return super.onStartCommand(intent, flags, startId);
    }
    void sendNotif() {
        String textNotif = "Oops!";
        switch (serviceState){
            case IDLE:
                textNotif = "Service start";
                break;
            case SAVED:
                textNotif = filename + " saved";
                break;
            case RECORDING:
                textNotif = "Recording...";
                break;
        }
        // 1-я часть
        Intent intent = new Intent(this, MainService.class);
        intent.putExtra(START_FROM_NOTIFICATION,true);
        PendingIntent pIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(textNotif)
                .setContentIntent(pIntent)
                .setAutoCancel(false);
        Notification notif = builder.build();
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableVibration(true);
        notificationManager.createNotificationChannel(notificationChannel);

        // 3-я часть

        notificationManager.notify("1", 1, notif);
    }
    void someTask(){
        Context context = getApplicationContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View view = new LinearLayout(context);
        WindowManager.LayoutParams viewParams = new WindowManager.LayoutParams(1, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                0,0,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        //saveImage();
        view.setLayoutParams(viewParams);
        wm.addView(view,params);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(fileBuilder != null) {
                    fileBuilder.putEvent(event);
                }
                return false;
            }
        });
    }
    private void saveImage() {
        Context context = getApplicationContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View view = new LinearLayout(context);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams viewParams = new WindowManager.LayoutParams(1, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                dm.widthPixels,dm.heightPixels,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        view.setLayoutParams(viewParams);
        wm.addView(view,params);
        view.setBackgroundColor(Color.TRANSPARENT);
        String path = filename + ".jpg";
        Bitmap bitmap = Bitmap.createBitmap(dm.widthPixels,dm.heightPixels, Bitmap.Config.ARGB_8888);
        Log.d("ZZZ","bitmap: " + bitmap.toString());

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
                try{
                    FileOutputStream outputStream = openFileOutput(path, Context.MODE_PRIVATE);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }catch(Throwable e){
                    e.printStackTrace();
                }



    }
}
