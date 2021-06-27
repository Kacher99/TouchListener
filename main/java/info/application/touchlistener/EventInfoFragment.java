package info.application.touchlistener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class EventInfoFragment extends Fragment {
    private TextView textInfo;
    private Button saveButton;
    private ImageView img;
    private Activity activity;
    private EditText editText;
    private TextView events;
    private EventInfoFragment(){}
    private static final String FILENAME = "filename";
    public static EventInfoFragment getInstance(String filename){
        Bundle bundle = new Bundle();
        bundle.putString(FILENAME, filename);
        EventInfoFragment fragment = new EventInfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.event_info_layout, parent, false);
        textInfo = view.findViewById(R.id.textInfo);
        saveButton = view.findViewById(R.id.saveButton);
        img = view.findViewById(R.id.imageView);
        editText=view.findViewById(R.id.textEdit);
        events = view.findViewById(R.id.events);
        String filename = getArguments().getString(FILENAME);
        FileBuilder fileBuilder = new FileBuilder();
        fileBuilder.ReadFromFile(filename, parent.getContext());
        textInfo.setText("Date: " + fileBuilder.getDate() +
                "\nProcess time(ms): " + fileBuilder.getProcessTime());
        events.setText(fileBuilder.getArray());
        editText.setText(fileBuilder.getComment());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileBuilder.setComment(editText.getText().toString());
                fileBuilder.SaveToFile(filename,parent.getContext());
            }
        });
        activity.setTitle(filename);
        return view;
    }
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof Activity)
        {
            activity = (Activity) context;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.setTitle(R.string.app_name);
    }
}
