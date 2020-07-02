package info.wiki.com.wiki.helper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import info.wiki.com.wiki.entity.ItemClass;

public class ObjectSaver extends AsyncTask<Void, Integer, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private ItemClass itemClass;
    private SQLiteDatabase database;
    private String PATHS = "";
    private ProgressDialog dialog;
    public static String FOLDER_NAME = "/Encyclopedia/";
    public static final String SPACE_IDENTIFIER = " SPACE_IDENTIFIER ";
    public static final int QUALITY = 10;
    public static final int NO_SAVE_IMAGES = 4;

    public ObjectSaver(Context context, ItemClass itemClass, SQLiteDatabase database) {
        this.context = context;
        this.itemClass = itemClass;
        this.database = database;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Bitmap bitmap = null;
        int noofimages = 0;
        for (String URL : itemClass.imageListURLS) {
            try {
                java.net.URL url = new URL(URL);
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME + itemClass.heading + "/";
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                Date date = new Date();
                String IMAGENAME = date.getTime() + "";
                File file1 = new File(file, IMAGENAME + ".png");
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                PATHS += path + IMAGENAME + ".png" + SPACE_IDENTIFIER;
                noofimages++;
                publishProgress(noofimages);
                if (noofimages == NO_SAVE_IMAGES)
                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        dialog.setMessage("Downloaded Images : "+values[0]+" of "+NO_SAVE_IMAGES);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dialog.dismiss();

        //OBJECT OF CONTENT VALUES
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.HEADING, itemClass.heading);
        contentValues.put(DatabaseHelper.SUMMARY, itemClass.summary);
        contentValues.put(DatabaseHelper.IMAGES, PATHS);
        contentValues.put(DatabaseHelper.STAMP, itemClass.stamp);

        //SAVE TO DATABASE
        database.insert(DatabaseHelper.TABLENAME, null, contentValues);

        Toast.makeText(context, "Topic Saved Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading Resources Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

}
