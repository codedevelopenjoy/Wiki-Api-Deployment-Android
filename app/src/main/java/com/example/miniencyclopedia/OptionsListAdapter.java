package com.example.miniencyclopedia;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OptionsListAdapter extends RecyclerView.Adapter<OptionsListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> optionsList;

    public OptionsListAdapter(Context context, ArrayList<String> optionsList) {
        this.context = context;
        this.optionsList = optionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.options_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.textView.setText(optionsList.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.editText.setText(holder.textView.getText());
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.option_item_text);
        }
    }
}
