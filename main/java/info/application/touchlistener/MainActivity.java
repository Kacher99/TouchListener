package info.application.touchlistener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private AdapterInfo adapter;
    private EventInfoFragment fragment;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            changeAdapter();
        }
    }

    List<Info> list = new ArrayList<>();
    private void createTestFile() {
        FileBuilder fileBuilder = new FileBuilder();
        fileBuilder.logDate();
        fileBuilder.setScreen("123");
        fileBuilder.setComment("123");
        fileBuilder.putEvent(MotionEvent.obtain(new Date().getTime(), 0, MotionEvent.ACTION_DOWN,
                0, 0, 0));
        fileBuilder.SaveToFile(FileManager.generateFileName(), this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Log.d("222","==null");
            startService(new Intent(this, MainService.class));
            adapter = new AdapterInfo(list, this, (info, position) -> {
                fragment = EventInfoFragment.getInstance(info.getName());
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.main_layout, fragment, "LOGIN_TAG")
                        .addToBackStack(null)
                        .commit();
            });
            //createTestFile();


        setContentView(R.layout.activity_main);
        RecyclerView view = findViewById(R.id.list_view);
        view.setAdapter(adapter);

    }

    private void changeAdapter() {
        list.clear();
        FileManager fileManager = new FileManager(this);
        for (String str : fileManager.getList()) {
            FileBuilder fileBuilder = new FileBuilder();
            fileBuilder.ReadFromFile(str, this);
            list.add(new Info(str, fileBuilder.getComment().split("\n",2)[0], R.mipmap.ic_launcher));
        }
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_clearAll:
                new FileManager(this).deleteAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}