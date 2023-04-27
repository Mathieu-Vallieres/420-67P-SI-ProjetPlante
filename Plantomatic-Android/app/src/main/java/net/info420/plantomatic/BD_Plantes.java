package net.info420.plantomatic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import java.net.URI;

public class BD_Plantes {

    private static final String TAG = BD_Plantes.class.getSimpleName();

    Context context;
    DBHelper dbHelper;
    SQLiteDatabase db;
    public static final String DB_NAME = "plantomatic.db";
    public static final String TABLE_NAME = "bd_plantes";
    public static final int DB_VERSION = 1;


    public static final String C_ID = BaseColumns._ID;
    public static final String C_IMAGE = "image";
    public static final String C_NOMPLANTE = "nomPlante";
    public static final String C_HUMIDITY = "humidity";
    public static final String C_ML_EAU = "mlEau";

    public BD_Plantes(Context context) {
        this.context = context;
        dbHelper = new DBHelper();
    }

    public void insert (Uri imagePlante, String nomPlante, int humidite, int quantiteEau){
        ContentValues fieldsValues = new ContentValues();
        db = dbHelper.getWritableDatabase();
        fieldsValues.put(C_IMAGE, imagePlante.toString());
        fieldsValues.put(C_NOMPLANTE, nomPlante);
        fieldsValues.put(C_HUMIDITY, humidite);
        fieldsValues.put(C_ML_EAU, quantiteEau);

        db.insertWithOnConflict(TABLE_NAME, null, fieldsValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public Cursor query(){
        Cursor cursor;
        db = dbHelper.getReadableDatabase();

        cursor = db.query(TABLE_NAME, null, null, null, null, null, C_ID + " DESC");
        return cursor;
    }
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper() {
            super(context,DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql; // Variable pour les requêtes SQL

            sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER)",
                    TABLE_NAME, C_ID, C_IMAGE ,C_NOMPLANTE, C_HUMIDITY, C_ML_EAU);

            db.execSQL(sql);
            Log.d(TAG, String.format("onCreate(): La table %s a été créée avec succès.", TABLE_NAME));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
