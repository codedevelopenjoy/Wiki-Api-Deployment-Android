package info.wiki.com.wiki;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import info.wiki.com.wiki.entity.Settings;
import info.wiki.com.wiki.helper.PrefsHelper;
import top.defaults.colorpicker.ColorPickerPopup;

public class SettingsActivity extends AppCompatActivity {

    //GLOBAL VARS DECLARATION
    private ImageView p_f_color, p_b_color;
    private TextView  p_f_family, family, quality;
    private EditText size, folder;
    public static final String[] FAMILIES = {"Monospace(R)","Allura","Great-Vibes","Lobster","OpenSans","Ostrich","PlayFair","QuickSand","Roboto"};
    public static final String[] QUALITIES = {"Ultra Low","Low", "Medium", "High", "Ultra High"};
    public Settings currentSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //INIT VIEWS
        p_f_color = findViewById(R.id.i_p_color);
        p_b_color = findViewById(R.id.i_p_bcolor);
        p_f_family = findViewById(R.id.tv_p_family);
        family = findViewById(R.id.family);
        size = findViewById(R.id.size);
        folder = findViewById(R.id.savefolder);
        quality = findViewById(R.id.quality);

        //FETCH CURRENT SETTINGS
        PrefsHelper prefsHelper = new PrefsHelper(SettingsActivity.this);
        currentSettings = prefsHelper.getSettings();

        //LOAD DEFAULT SETTINGS
        loadSettingstoPage();

        //ATTACH LISTENERS
        p_f_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ColorPickerPopup.Builder(SettingsActivity.this)
                        .initialColor(Integer.parseInt(currentSettings.fontColor))
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onColorPicked(int color) {
                                currentSettings.fontColor = color + "";
                                p_f_color.setBackgroundColor(Integer.parseInt(currentSettings.fontColor));
                            }
                        });
            }
        });

        p_b_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new ColorPickerPopup.Builder(SettingsActivity.this)
                        .initialColor(Integer.parseInt(currentSettings.backColor))
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onColorPicked(int color) {
                                currentSettings.backColor = color + "";
                                p_b_color.setBackgroundColor(Integer.parseInt(currentSettings.backColor));
                            }
                        });

            }
        });
        family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SHOW LIST OF AVAILABLE FAMILIES
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Choose Font Family");
                builder.setItems(FAMILIES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentSettings.fontFamily = FAMILIES[i];
                        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), String.format(Locale.US, "%s", currentSettings.fontFamily + ".ttf"));
                        p_f_family.setTypeface(typeface);
                        family.setText(FAMILIES[i]);
                    }
                });
                builder.create().show();

            }
        });
        quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SHOW LIST OF AVAILABLE FAMILIES
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Choose Image Quality");
                builder.setItems(QUALITIES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                currentSettings.imageQuality = "20";
                                break;
                            case 1:
                                currentSettings.imageQuality = "40";
                                break;
                            case 2:
                                currentSettings.imageQuality = "60";
                                break;
                            case 3:
                                currentSettings.imageQuality = "80";
                                break;
                            case 4:
                                currentSettings.imageQuality = "100";
                                break;
                        }
                        quality.setText(QUALITIES[i]);
                    }
                });
                builder.create().show();

            }
        });
    }

    private void loadSettingstoPage() {
        size.setText(currentSettings.fontSize);
       p_f_color.setBackgroundColor(Integer.parseInt(currentSettings.fontColor));
        p_b_color.setBackgroundColor(Integer.parseInt(currentSettings.backColor));
        family.setText(currentSettings.fontFamily);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), String.format(Locale.US, "%s", currentSettings.fontFamily + ".ttf"));
        p_f_family.setTypeface(typeface);
        folder.setText(currentSettings.folderName);
        switch(currentSettings.imageQuality){
            case "20":
                quality.setText(QUALITIES[0]);
                break;
            case "40":
                quality.setText(QUALITIES[1]);
                break;
            case "60":
                quality.setText(QUALITIES[2]);
                break;
            case "80":
                quality.setText(QUALITIES[3]);
                break;
            case "100":
                quality.setText(QUALITIES[4]);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveSettings();
                break;
        }
        return true;
    }

    private void saveSettings() {

        if (size.getText().toString().isEmpty()) {
            size.setText(currentSettings.fontSize);
            Toast.makeText(SettingsActivity.this, "Size Can't be Empty...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (folder.getText().toString().isEmpty()) {
            folder.setText(currentSettings.folderName);
            Toast.makeText(this, "Folder name Can't be Empty...", Toast.LENGTH_SHORT).show();
            return;
        }

        currentSettings.fontSize = size.getText().toString();

        currentSettings.folderName = folder.getText().toString();
        PrefsHelper prefsHelper = new PrefsHelper(SettingsActivity.this);
        prefsHelper.addSettings(currentSettings);
        Toast.makeText(this, "Settings Successfully Saved...", Toast.LENGTH_SHORT).show();
        finish();
    }
}
