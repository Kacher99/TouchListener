package info.application.touchlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Start extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Context mContext = context.getApplicationContext();
        Log.d("XXX","Receive");
        Log.d("XXX",String.format("context:%s\nintent:%s",context.toString(),intent.toString()));
        mContext.startService(new Intent(mContext, MainService.class));
    }
}
