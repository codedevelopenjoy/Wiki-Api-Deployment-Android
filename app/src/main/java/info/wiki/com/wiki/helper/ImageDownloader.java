package info.wiki.com.wiki.helper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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

public class ImageDownloader extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private String URL,QUERY,IMAGENAME;
    private ProgressDialog dialog;
    public static String FOLDER_NAME = "/Encyclopedia/";
    public static int QUALITY = 60;

    public ImageDownloader(Context context, String URL, String QUERY) {
        this.context = context;
        this.URL = URL;
        this.QUERY = QUERY;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(URL);
            InputStream inputStream = url.openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME + QUERY + "/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            Date date = new Date();
            IMAGENAME = date.getTime() + "";
            File file1 = new File(file, IMAGENAME + ".png");
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dialog.dismiss();
        Toast.makeText(context, "Image Downloaded Successfully as "+IMAGENAME + ".png", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading Image...");
        dialog.setCancelable(false);
        dialog.show();
    }
}