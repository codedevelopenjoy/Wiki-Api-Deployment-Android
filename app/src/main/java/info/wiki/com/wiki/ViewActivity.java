package info.wiki.com.wiki;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import java.io.File;
import java.util.Locale;
import info.wiki.com.wiki.entity.ItemClass;
import info.wiki.com.wiki.entity.Settings;
import info.wiki.com.wiki.helper.DatabaseHelper;
import info.wiki.com.wiki.helper.ImageDownloader;
import info.wiki.com.wiki.helper.ObjectSaver;
import info.wiki.com.wiki.helper.PrefsHelper;

public class ViewActivity extends AppCompatActivity {

    //GLOBAL VARS DECLARATION
    private CarouselView carouselView;
    private TextView summary, timestamp;
    private ScrollView layoutfull;
    private ItemClass itemClass;
    public static final String OBJECT_IDENTIFIER = "ITEM_CLASS_OBJECT";
    private int REQUEST_CODE_WRITE = 1000;
    private int TEXTSIZE = 16;
    private boolean isOnline;
    public static final String NETWORK_IDENTIFIER = "NETWORK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        //INIT VIEWS
        carouselView = findViewById(R.id.carouselView);
        summary = findViewById(R.id.summary);
        timestamp = findViewById(R.id.stamp);
        layoutfull = findViewById(R.id.layoutfull);

        //GET ITEM OBJECT TO VIEW
        if (getIntent().getExtras() != null) {

            itemClass = (ItemClass) getIntent().getExtras().get(OBJECT_IDENTIFIER);
            isOnline = (boolean) getIntent().getExtras().get(NETWORK_IDENTIFIER);

            //IF OBJECT IS NOT NULL
            if (itemClass != null) {

                //SUMMARY STAMP AND TITLE SET
                summary.setText(itemClass.summary);
                timestamp.setText(itemClass.stamp);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(itemClass.heading.toUpperCase());
                }

                //SET IMAGES
                carouselView.setPageCount(itemClass.imageListURLS.size());
                carouselView.setImageListener(new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        //LOAD IMAGES ON BY ONE
                        if (!isOnline)
                            Picasso.get().load(Uri.fromFile(new File(itemClass.imageListURLS.get(position)))).into(imageView);
                        else
                            Picasso.get().load(itemClass.imageListURLS.get(position)).into(imageView);
                    }
                });
                carouselView.setImageClickListener(new ImageClickListener() {
                    @Override
                    public void onClick(int position) {
                        Intent intent = new Intent(ViewActivity.this, ImageViewActivity.class);
                        intent.putExtra(ImageViewActivity.URL_IDENTIFIER, itemClass.imageListURLS.get(position));
                        intent.putExtra(ImageViewActivity.HEADING_IDENTIFIER, itemClass.heading);
                        intent.putExtra(NETWORK_IDENTIFIER, isOnline);
                        startActivity(intent);
                    }
                });

            } else {
                Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            finish();
        }

        //FETCH SETTING AND GET
        getSettingsAndSet();
    }

    private void getSettingsAndSet() {
        PrefsHelper prefsHelper = new PrefsHelper(ViewActivity.this);
        Settings settings = prefsHelper.getSettings();

        //FONT SIZE
        TEXTSIZE = Integer.parseInt(settings.fontSize);
        summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXTSIZE);
        //FONT COLOR
        summary.setTextColor(Integer.parseInt(settings.fontColor));
        //BACKGROUND COLOR
        layoutfull.setBackgroundColor(Integer.parseInt(settings.backColor));
        //TYPEFACE
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), String.format(Locale.US, "%s", settings.fontFamily + ".ttf"));
        summary.setTypeface(typeface);
        //CHAGE FOLDER NAME
        ImageDownloader.FOLDER_NAME = settings.folderName;
        ObjectSaver.FOLDER_NAME = settings.folderName;
        //CHANGE QUALITY
        ImageDownloader.QUALITY = Integer.parseInt(settings.imageQuality);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewsaved:
                viewSavedActivity();
                break;
            case R.id.save:
                startSave();
                break;
            case R.id.fontsettings:
                Intent intent = new Intent(ViewActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.zoomin:
                summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXTSIZE++);
                break;
            case R.id.zoomout:
                summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXTSIZE--);
                break;
        }
        return true;
    }

    private void viewSavedActivity() {
        Intent intent = new Intent(ViewActivity.this, ViewSavedActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSettingsAndSet();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSettingsAndSet();
    }

    private void startSave() {

        //ASK FOR CONFIRMATION
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewActivity.this);
        builder.setTitle("Confirm?");
        builder.setMessage("Are you sure want to Save (Require Storage Permissions)?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //FIRST CHECK FOR WRITE PERMISSION AND SAVE
                if (checkMyPermission()) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(ViewActivity.this, DatabaseHelper.DB_NAME, null, 1);
                    databaseHelper.addItem(itemClass);
                } else {
                    //SHOW PERMISSION DIALOG
                    ActivityCompat.requestPermissions(ViewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE);
                }

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
                DatabaseHelper databaseHelper = new DatabaseHelper(ViewActivity.this, DatabaseHelper.DB_NAME, null, 1);
                databaseHelper.addItem(itemClass);
            }
        }
    }


}
