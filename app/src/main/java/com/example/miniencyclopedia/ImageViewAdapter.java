package com.example.miniencyclopedia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> {
    private Context context;
    private  ArrayList<String> imagesURLs;

    public ImageViewAdapter(Context context, ArrayList<String> imagesURLs) {
        this.context = context;
        this.imagesURLs = imagesURLs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.valid_list_item_image,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Glide.with(context).asBitmap().load(imagesURLs.get(position)).into(holder.mainImage);
        holder.mainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBaseClass.imageOfPosClicked = imagesURLs.get(position);
                Intent intent =new Intent(context,ImageViewActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagesURLs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView downloadImage,mainImage;
        TextView textView;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mainImage = itemView.findViewById(R.id.valid_item_image);
        }
    }
}
