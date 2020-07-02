package info.wiki.com.wiki;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.File;
import info.wiki.com.wiki.helper.ImageDownloader;

import static info.wiki.com.wiki.ViewActivity.NETWORK_IDENTIFIER;

public class ImageViewActivity extends AppCompatActivity {

    //GLOBAL VARS DECLARATION
    private ImageView imageView;
    private ProgressBar progressBar;
    private String ImageURL;
    private String Heading;
    private boolean isOnline;
    private int REQUEST_CODE_WRITE = 1000;
    public static final String URL_IDENTIFIER = "URL";
    public static final String HEADING_IDENTIFIER = "HEADING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        //INIT VIEWS
        imageView = findViewById(R.id.image);
        progressBar = findViewById(R.id.progress);

        //GET DATA AND SET
        if (getIntent().getExtras() != null) {

            //GET DATA
            ImageURL = getIntent().getExtras().getString(URL_IDENTIFIER);
            Heading = getIntent().getExtras().getString(HEADING_IDENTIFIER);
            isOnline = (boolean) getIntent().getExtras().get(NETWORK_IDENTIFIER);

            //LOAD IMAGE

            if (isOnline) {
                Picasso.get().load(ImageURL).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(ImageViewActivity.this, "Image Loading Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            else {
                Picasso.get().load(Uri.fromFile(new File(ImageURL))).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(ImageViewActivity.this, "Image Loading Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            //SET TITLE AS HEADING
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Heading.toUpperCase());
            }

        } else {
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download:
                startDownload();
                break;
        }
        return true;
    }

    private void startDownload() {
        //IF OFFLINE ALREADY SHOW TOAST
        if(!isOnline){
            Toast.makeText(this, "Image Already Downloaded\nPath = "+ImageURL, Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkMyPermission()) {
            ImageDownloader imageDownloader = new ImageDownloader(ImageViewActivity.this, ImageURL, Heading);
            imageDownloader.execute();
        } else {
            //SHOW PERMISSION DIALOG
            ActivityCompat.requestPermissions(ImageViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE);
        }
    }

    private boolean checkMyPermission() {
        int write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return write == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE) {
            if (!checkMyPermission()) {
                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            } else {
                ImageDownloader imageDownloader = new ImageDownloader(ImageViewActivity.this, ImageURL, Heading);
                imageDownloader.execute();
            }
        }
    }

}
