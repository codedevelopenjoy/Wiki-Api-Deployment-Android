package info.wiki.com.wiki.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import info.wiki.com.wiki.MainActivity;
import info.wiki.com.wiki.ViewActivity;
import info.wiki.com.wiki.entity.ItemClass;

public class DataHandler extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String query;
    private ProgressDialog dialog;
    private JSONObject main = null;
    private static final String BASE_URL = "https://wikiapi.herokuapp.com/?k=MVwyYghfeMXlJOAP2LuYmPYuV&l=en&q=";

    public DataHandler(Context context, String query) {
        this.context = context;
        this.query = query;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (main == null) {
            Toast.makeText(context, "Internet not Connected", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        dialog.dismiss();

        try {
            switch (Integer.parseInt(main.get("code").toString())) {

                case 1:
                    ItemClass itemClass = new ItemClass();

                    //SET SUMMARY AND HEADING AND STAMP
                    itemClass.summary = main.get("content").toString();
                    itemClass.heading = query;
                    itemClass.stamp = new Date().toString();

                    //SET IMAGES URL
                    JSONObject images = new JSONObject(main.get("images").toString());
                    ArrayList<String> imagesURL = new ArrayList<>();
                    String key;
                    for (Iterator<String> it = images.keys(); it.hasNext(); ) {
                        key = it.next();
                        imagesURL.add(images.get(key).toString());
                    }
                    itemClass.imageListURLS = imagesURL;

                    //SAVE CURRENT QUERY TO SHARED PREFS
                    PrefsHelper prefsHelper = new PrefsHelper(context);
                    prefsHelper.addNewItemRecent(query);

                    //GET ALL RECENT LIST AND ADD TO LISTVIEW
                    ((MainActivity) context).showAllRecentItems();

                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra(ViewActivity.OBJECT_IDENTIFIER, itemClass);
                    intent.putExtra(ViewActivity.NETWORK_IDENTIFIER,true);
                    context.startActivity(intent);
                    break;

                case 2:
                    //GETTING OPTIONS FROM OUTPUT
                    JSONObject object = main.getJSONObject("options");
                    String s;
                    final String[] options = new String[object.length()];
                    int index = 0;
                    for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                        s = it.next();
                        options[index++] = object.get(s).toString();
                    }
                    //--------------------------

                    //SHOW IN ALERT DIALOG AND START FOR NEW RESULT
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose Topic");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataHandler dataHandler = new DataHandler(context, options[i]);
                            dataHandler.execute();
                        }
                    });
                    builder.create().show();
                    //----------------------------------------------

                    break;

                case 3:
                    //NO DATA FOUND
                    Toast.makeText(context, "Data Not Found\nTry Something Else", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    //ERROR OCCURRED
                    Toast.makeText(context, "Unknown error occurred", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Fetching Data");
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //MODIFY QUERY WITH %20
            String new_query = "";
            for (String s : query.trim().split(" ")) {
                new_query = new_query + s + "%20";
            }
            //-----------------------

            //GET OUTPUT FROM API
            URL url = new URL(BASE_URL + new_query);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            main = new JSONObject(sb.toString());
            //---------------------
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
