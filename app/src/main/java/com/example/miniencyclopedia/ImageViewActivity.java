package com.example.miniencyclopedia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView imageView,downloadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.finalImage);
        downloadImage = findViewById(R.id.downloadImage);

        Glide.with(this).asBitmap().load(DataBaseClass.imageOfPosClicked).into(imageView);
        downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Download image
                Download download = new Download();
                download.execute();
            }
        });

    }

    class Download extends AsyncTask<Void,Void,Void>{

        ProgressDialog dialog =new ProgressDialog(ImageViewActivity.this);

        @Override
        protected Void doInBackground(Void... voids) {
            Bitmap bitmap = null;
            try {
                URL url =new URL(DataBaseClass.imageOfPosClicked);
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Encyclopedia/"+DataBaseClass.itemClass.query+"/";
                File file = new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                Date date = new Date();
                File file1 = new File(file,date.getTime()+".png");
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.PNG,80,fileOutputStream);
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
            Toast.makeText(ImageViewActivity.this, "Downloaded", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Downloading Image...");
            dialog.setCancelable(true);
            dialog.show();
        }
    }
}
