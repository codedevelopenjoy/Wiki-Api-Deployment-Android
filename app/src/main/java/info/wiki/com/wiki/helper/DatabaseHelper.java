package info.wiki.com.wiki.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import info.wiki.com.wiki.entity.ItemClass;

public class DatabaseHelper extends SQLiteOpenHelper{

    private Context context;
    public static final String DB_NAME = "DB";
    public static final String TABLENAME = "topics";
    public static final String HEADING = "heading";
    public static final String SUMMARY = "summary";
    public static final String IMAGES = "images";
    public static final String STAMP = "stamp";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL = new StringBuilder().append("create table ").append(TABLENAME).append(" ( ").append(HEADING).append(" text,").append(SUMMARY).append(" text,").append(IMAGES).append(" text,").append(STAMP).append(" text)").toString();
        sqLiteDatabase.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String SQL = new StringBuilder().append("drop table if exists ").append(TABLENAME).toString();
        sqLiteDatabase.execSQL(SQL);
        onCreate(sqLiteDatabase);
    }

    public void addItem(ItemClass itemClass){
     ObjectSaver objectSaver = new ObjectSaver(context,itemClass,this.getWritableDatabase());
     objectSaver.execute();
    }

    @SuppressLint("Recycle")
    public ArrayList<ItemClass> getItems() {
        ArrayList<ItemClass> items = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(new StringBuilder().append("select * from ").append(TABLENAME).toString(),null);
        if(cursor!=null){
            while (cursor.moveToNext()){

                //MAKE ITEM CLASS OBJECT
                ItemClass itemClass = new ItemClass();
                itemClass.heading = cursor.getString(cursor.getColumnIndex(HEADING));
                itemClass.summary = cursor.getString(cursor.getColumnIndex(SUMMARY));
                itemClass.stamp = cursor.getString(cursor.getColumnIndex(STAMP));
                ArrayList<String> list = new ArrayList<>();
                for(String s : cursor.getString(cursor.getColumnIndex(IMAGES)).split(ObjectSaver.SPACE_IDENTIFIER)){
                    list.add(s.trim());
                    Log.d("TAG11", "getItems: "+s.trim());
                }
                itemClass.imageListURLS = list;

                //ADD TO LIST
                items.add(itemClass);
            }
        }
        return items;
    }

    public void deleteItem(String stamp){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLENAME,STAMP+" = ?",new String[]{stamp});
    }

}
