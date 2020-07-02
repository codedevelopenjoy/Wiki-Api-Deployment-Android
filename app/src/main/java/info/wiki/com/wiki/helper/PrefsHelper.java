package info.wiki.com.wiki.helper;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import info.wiki.com.wiki.entity.Settings;

public class PrefsHelper {

    public static final String PREFS_NAME = "Wiki";
    public static final String LIST_NAME = "Recent";
    public static final String F_SIZE = "F_SIZE";
    public static final String F_COLOR = "F_COLOR";
    public static final String F_FAMILY = "F_FAMILY";
    public static final String B_COLOR = "B_COLOR";
    public static final String FOLDER_NAME = "FOLDER_NAME";
    public static final String IMG_QUALITY = "IMG_QUALITY";
    private Context context;

    public PrefsHelper(Context context) {
        this.context = context;
    }

    public ArrayList<String> getRecentList(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet(LIST_NAME, null);
        if(set == null)
            return null;
        return new ArrayList<>(set);
    }

    public void addNewItemRecent(String query){
        ArrayList<String> list = getRecentList();
        if(list == null){
            list = new ArrayList<>();
            list.add(query);
        }
        else{
            if(!list.contains(query.trim()))
                list.add(query);
        }
        saveRecentSet(list);
    }

    private void saveRecentSet(ArrayList<String> list) {
        Set<String> set = new HashSet<>(list);
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(LIST_NAME,set);
        editor.apply();
    }

    public Settings getSettings() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Settings settings = new Settings();
        settings.fontSize = sharedPreferences.getString(F_SIZE, "16");
        settings.fontColor = sharedPreferences.getString(F_COLOR, "-16777216");
        settings.fontFamily = sharedPreferences.getString(F_FAMILY, "Monospace(R)");
        settings.backColor = sharedPreferences.getString(B_COLOR, "-1");
        settings.folderName = sharedPreferences.getString(FOLDER_NAME, ImageDownloader.FOLDER_NAME);
        settings.imageQuality = sharedPreferences.getString(IMG_QUALITY, "60");
        return settings;
    }

    public void addSettings(Settings settings){
        SharedPreferences Preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferences = Preferences.edit();
        sharedPreferences.putString(F_SIZE, settings.fontSize);
        sharedPreferences.putString(F_COLOR, settings.fontColor);
        sharedPreferences.putString(F_FAMILY, settings.fontFamily);
        sharedPreferences.putString(B_COLOR, settings.backColor);
        sharedPreferences.putString(FOLDER_NAME, settings.folderName);
        sharedPreferences.putString(IMG_QUALITY, settings.imageQuality);
        sharedPreferences.apply();
    }

}
