package info.application.touchlistener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterInfo extends RecyclerView.Adapter<AdapterInfo.ViewHolder> {
    private final List<Info> items;
    private final LayoutInflater inflater;
    private OnItemClickListener listener;
    interface OnItemClickListener{
        void onItemClick(Info info,int position);
    }
    AdapterInfo(List<Info> items, Context context, OnItemClickListener listener) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterInfo.ViewHolder holder, int position) {
        Info info = items.get(position);
        holder.screenImage.setImageResource(info.getScreen());
        holder.infoText.setText(info.getNote());
        holder.nameText.setText(info.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listener.onItemClick(info, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        final ImageView screenImage;
        final TextView nameText, infoText;


        ViewHolder(View view){
            super(view);
            screenImage = (ImageView)view.findViewById(R.id.main_image);
            nameText = (TextView)view.findViewById(R.id.main_text_name);
            infoText = (TextView)view.findViewById(R.id.main_text_info);
        }
    }
}
