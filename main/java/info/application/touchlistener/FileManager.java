package info.application.touchlistener;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FileManager {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYYMMdd_HHmmss");
    private Context context;
    FileManager(Context context){
        this.context = context;
    }
    static public String generateFileName(){
        StringBuilder sb = new StringBuilder();
        sb.append(DATE_FORMAT.format(new Date()));
        sb.append("_").append(new Random().nextInt());
        return sb.toString();
    }

    public List<String> getList(){
        return Arrays.asList(context.fileList().clone());
    }
    public void Delete(String name){
        context.deleteFile(name);
    }
    public void deleteAll(){
        for (String s : getList()) {
            Delete(s);
        }
    }
}
