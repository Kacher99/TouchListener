package info.application.touchlistener;

import android.content.Context;
import android.util.JsonReader;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileBuilder {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYYMMdd_HHmmss");
    public static final String START_DATE ="StartDate";
    public static final String COMMENT ="Comment";
    public static final String IMAGE ="Image";
    public static final String PROCESS_TIME ="ProcessTime";
    public static final String JARRAY ="Events";
    public static final String EVENT_TIME ="EventTime";
    public static final String X ="X";
    public static final String Y ="Y";
    public static final String EVENT_ACTION ="EventAction";


    private JSONObject jsonObject;
    private JSONArray events;
    private long lastEventTime;
    private long firstEventTime;
    FileBuilder(){
        jsonObject = new JSONObject();
        events = new JSONArray();
        try {
            jsonObject.put(COMMENT, "");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void logDate(){
        try {
            jsonObject.put(START_DATE, new Date().getTime());
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public String getDate(){
        try {
            Date date = new Date(Long.valueOf(jsonObject.get(START_DATE).toString()));
            return  DATE_FORMAT.format(date);
        }catch(JSONException  | NumberFormatException e){
            e.printStackTrace();
        }
        return "";
    }
    public void setScreen(String str){
        setAll(IMAGE,str);
    }
    public String getScreen(){
        return getAll(IMAGE);
    }
    public void setComment(String str){
        setAll(COMMENT,str);
    }
    public String getComment(){
        return getAll(COMMENT);
    }
    private void setAll(String s1,String s2){
        try {
            jsonObject.put(s1, s2);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    private String getAll(String s1){
        try {
            return jsonObject.getString(s1);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return "";
    }
    public long getProcessTime(){
        try {
            long processTime = Long.valueOf(jsonObject.get(PROCESS_TIME).toString());
            return processTime;
        }catch(JSONException e){
            e.printStackTrace();
        }
        return 0;
    }
    public void putEvent(MotionEvent ev){
        JSONObject event = new JSONObject();
        lastEventTime = ev.getDownTime();
        if(events.length() == 0){
            firstEventTime = lastEventTime;
        }
        try {
            event.put(EVENT_TIME, lastEventTime);
            event.put(X, ev.getX());
            event.put(Y, ev.getY());
            event.put(EVENT_ACTION, ev.getAction());
            events.put(event);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void build(){
        try {
            jsonObject.put(START_DATE,firstEventTime);
            jsonObject.put(PROCESS_TIME, lastEventTime - firstEventTime);
            jsonObject.put(JARRAY, events);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public void SaveToFile(String name,Context context){
        build();
        try {
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            fos.write(toString().getBytes("UTF-8"));
            fos.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void ReadFromFile(String str,Context context){
        JsonReader jsonReader;
        try {
            InputStream is = context.openFileInput(str);
            jsonReader = new JsonReader(new InputStreamReader(is,"UTF-8"));
            jsonReader.beginObject();
            while(jsonReader.hasNext()){
                String name = jsonReader.nextName();
                if(name.equals(IMAGE)){
                    jsonObject.put(IMAGE,jsonReader.nextString());
                }else if(name.equals(COMMENT)){
                    jsonObject.put(COMMENT,jsonReader.nextString());
                }else if(name.equals(START_DATE)){
                    jsonObject.put(START_DATE,jsonReader.nextLong());
                }else if(name.equals(PROCESS_TIME)){
                    jsonObject.put(PROCESS_TIME,jsonReader.nextLong());
                }else if(name.equals(JARRAY)){
                    ReadJSONArray(jsonReader);
                }
            }
            jsonReader.endObject();
        }catch(IOException | JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

    private void ReadJSONArray(JsonReader reader) throws IOException, JSONException {
        reader.beginArray();
        while(reader.hasNext()){
            JSONObject event = new JSONObject();
            reader.beginObject();
            while(reader.hasNext()){
                String name = reader.nextName();
                if(name.equals(EVENT_TIME)){
                    event.put(EVENT_TIME,reader.nextLong());
                }else if(name.equals(X)){
                    event.put(X,reader.nextDouble());
                }else if(name.equals(Y)){
                    event.put(Y,reader.nextDouble());
                }else if(name.equals(EVENT_ACTION)) {
                    event.put(EVENT_ACTION, reader.nextInt());
                }
            }
            reader.endObject();
            events.put(event);
        }
        reader.endArray();
    }
    public String getArray(){
        return events.toString();
    }
}
