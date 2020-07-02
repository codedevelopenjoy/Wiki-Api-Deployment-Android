package info.wiki.com.wiki;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import info.wiki.com.wiki.entity.ItemClass;
import info.wiki.com.wiki.helper.DatabaseHelper;

public class ViewSavedActivity extends AppCompatActivity {

    //GLOBAL VARS DECLARATION
    private RecyclerView recyclerView;
    private ViewAllAdapter adapter;
    private ArrayList<ItemClass> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved);

        //INIT VIEWS
        recyclerView = findViewById(R.id.recyclerView);

        //SET RECYCLER VIEW SETTINGS
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        //GET ALL LIST
        DatabaseHelper databaseHelper = new DatabaseHelper(ViewSavedActivity.this,DatabaseHelper.DB_NAME,null,1);
        items = databaseHelper.getItems();

        //SET DATA
        adapter = new ViewAllAdapter(ViewSavedActivity.this,items);
        recyclerView.setAdapter(adapter);

    }
}
