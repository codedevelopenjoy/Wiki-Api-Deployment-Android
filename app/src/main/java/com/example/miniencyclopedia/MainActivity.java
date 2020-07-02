package com.example.miniencyclopedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static Button buttonView;
    public static EditText editText;
    private OptionsListAdapter adapter;
    private static final String BASE_URL = "https://wikiapi.herokuapp.com/?k=MVwyYghfeMXlJOAP2LuYmPYuV&l=en&q=";
    ArrayList<String> options = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init view
        buttonView = findViewById(R.id.searchIcon);
        editText = findViewById(R.id.searchText);
        recyclerView = findViewById(R.id.recycler_option_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().trim().isEmpty()){
                    DataHandler handler =new DataHandler(MainActivity.this,editText.getText().toString().toLowerCase().trim());
                    handler.execute();
                }
            }
        });

        options.clear();
        adapter = new OptionsListAdapter(this,options);
        recyclerView.setAdapter(adapter);
    }

    class DataHandler extends AsyncTask<Void,Void,Void>{
        Context context;
        String query;
        ProgressDialog dialog;
        JSONObject main = null;

        public DataHandler(Context context, String query) {
            this.context = context;
            this.query = query;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String new_query = "";
                String[] S =query.split(" ");
                for(String s : S){
                    new_query = new_query + s +"%20";
                }
                query = new_query;
                URL url =new URL(BASE_URL+query);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line = "";
                StringBuilder sb =new StringBuilder();
                while((line=reader.readLine())!=null){
                    sb.append(line);
                }
                main = new JSONObject(sb.toString());
                publishProgress();
            } catch (Exception e) {
                Log.d("ANSHUMAN", "doInBackground: "+e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {



            try {
                int x  = Integer.parseInt(main.get("code").toString());
                switch (x){
                    case 1:
                        ItemClass itemClass = new ItemClass();
                        itemClass.summary = main.get("content").toString();
                        JSONObject images = new JSONObject(main.get("images").toString());
                        ArrayList<String> imagesURL = new ArrayList<>();
                        ArrayList<String> imagesTitle  = new ArrayList<>();
                        String key;
                        int count=0;
                        for (Iterator<String> it = images.keys(); it.hasNext(); ) {
                            key = it.next();
                            imagesURL.add(images.get(key).toString());
                            imagesTitle.add(key.split("/")[key.split("/").length - 1]);
                            if(10==count++){
                                break;
                            }
                        }
                        itemClass.imageList = imagesURL;
                        itemClass.imageTitles = imagesTitle;
                        itemClass.query=query;
                        DataBaseClass.itemClass = itemClass;
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Intent intent =new Intent(MainActivity.this,ValidSearchActivity.class);
                        startActivity(intent);

                        break;
                    case 2:
                        JSONObject object = main.getJSONObject("options");
                        String s;
                        options.clear();
                        for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                            s = it.next();
                            options.add(object.get(s).toString());
                        }
                       adapter.notifyDataSetChanged();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        break;
                    case 3:
                        Toast.makeText(context, "Data not found for - "+query, Toast.LENGTH_SHORT).show();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        break;
                    case 4:
                    case 5:
                        Toast.makeText(context, "Unknown error occured", Toast.LENGTH_SHORT).show();
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        break;
                    default:
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        Toast.makeText(context, "Unknown error occured", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching Details");
            dialog.setTitle("Loading");
            dialog.setCancelable(false);
            dialog.show();
        }
    }

}
