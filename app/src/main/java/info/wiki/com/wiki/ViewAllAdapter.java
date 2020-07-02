package info.wiki.com.wiki;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import info.wiki.com.wiki.entity.ItemClass;
import info.wiki.com.wiki.helper.DatabaseHelper;

public class ViewAllAdapter extends RecyclerView.Adapter<ViewAllAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ItemClass> items;

    public ViewAllAdapter(Context context, ArrayList<ItemClass> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_saved, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.heading.setText(items.get(position).heading.toUpperCase());
        viewHolder.stamp.setText(items.get(position).stamp);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra(ViewActivity.OBJECT_IDENTIFIER, items.get(position));
                intent.putExtra(ViewActivity.NETWORK_IDENTIFIER,false);
                context.startActivity(intent);
            }
        });
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ASK FOR CONFIRMATION
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm?");
                builder.setMessage("Are you sure want to Delete?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(context, DatabaseHelper.DB_NAME, null, 1);
                        databaseHelper.deleteItem(items.get(position).stamp);
                        Toast.makeText(context, "Topic : " + items.get(position).heading + " Deleted Successfully...", Toast.LENGTH_SHORT).show();
                        items.remove(items.get(position));
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView heading, stamp;
        ImageView view, delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.heading);
            stamp = itemView.findViewById(R.id.stamp);
            view = itemView.findViewById(R.id.view);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
