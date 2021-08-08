package com.example.disaster_info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<NewsData> List = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textview;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.textView1);
            imageView = itemView.findViewById(R.id.imageView1);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        String url = List.get(pos).getUrl();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        void onBind(NewsData data){
            textview.setText(data.getText1());
            imageView.setImageResource(data.getImageid());
        }

    }

    void additem(NewsData data){
        List.add(data);
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    private OnItemClickListener listener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.news_recycleview_item, parent, false);
        NewsAdapter.ViewHolder viewHolder = new NewsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        holder.onBind(List.get(position));
    }

    @Override
    public int getItemCount() {
        return List.size();
    }
}
