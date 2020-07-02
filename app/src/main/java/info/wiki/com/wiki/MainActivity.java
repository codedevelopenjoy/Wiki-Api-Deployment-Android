package info.wiki.com.wiki;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import java.util.ArrayList;
import info.wiki.com.wiki.helper.DataHandler;
import info.wiki.com.wiki.helper.PrefsHelper;

public class MainActivity extends AppCompatActivity {

    //GLOBAL VARS DECLARATION
    private ListView recentList;
    private EditText inputQueryField;
    private ImageButton searchButton;
    private ArrayList<String> recentArrayList;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INIT VIEWS
        recentList = findViewById(R.id.listViewRecent);
        inputQueryField = findViewById(R.id.searchText);
        searchButton = findViewById(R.id.searchIcon);

        //INITIALIZE LIST
        recentArrayList = new ArrayList<>();

        //SET ADAPTER
        adapter = new ArrayAdapter<String>(this,R.layout.item_recent_search,recentArrayList);
        recentList.setAdapter(adapter);
        showAllRecentItems();

        //SET LISTENER FOR SEARCH
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //GET QUERY AND ENTER IF NOT NULL
                if(!inputQueryField.getText().toString().isEmpty()){
                    DataHandler dataHandler = new DataHandler(MainActivity.this,inputQueryField.getText().toString());
                    dataHandler.execute();
                }
            }
        });

        //SET LISTENER FOR LIST VIEW
        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataHandler dataHandler = new DataHandler(MainActivity.this,recentArrayList.get(i));
                dataHandler.execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fontsettings:
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.viewsaved:
                viewSavedActivity();
                break;
        }
        return true;
    }

    private void viewSavedActivity() {
        Intent intent = new Intent(MainActivity.this,ViewSavedActivity.class);
        startActivity(intent);
    }

    private ArrayList<String> getFromPreferences() {
        PrefsHelper prefsHelper = new PrefsHelper(this);
        ArrayList<String> items = prefsHelper.getRecentList();
        if (items == null){
            items  = new ArrayList<>();
            //TODO : ADD DEFAULT ITEMS LIST
            items.add("Facebook");
            items.add("Amazon");
            items.add("Sustainable Development");
            items.add("Mobile Phone");
            items.add("Mirage");
            items.add("Blisters");
            items.add("America");
            items.add("Democracy");
            items.add("India");
            items.add("Coal Mines");
            items.add("Corona Virus");
        }
        return items;
    }

    public void showAllRecentItems(){
        recentArrayList.clear();
        recentArrayList.addAll(getFromPreferences());
        adapter.notifyDataSetChanged();
    }
}
