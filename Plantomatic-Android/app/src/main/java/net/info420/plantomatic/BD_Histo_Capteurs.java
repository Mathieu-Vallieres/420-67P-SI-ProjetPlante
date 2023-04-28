package net.info420.plantomatic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class BD_Histo_Capteurs {

    private static final String TAG = BD_Histo_Capteurs.class.getSimpleName();

    Context context;
    BD_Histo_Capteurs.DBHelper dbHelper;
    SQLiteDatabase db;
    public static final String DB_NAME = "plantomatic.db";
    public static final String TABLE_NAME = "BD_Histo_Capteurs";
    public static final int DB_VERSION = 1;


    public static final String C_ID = BaseColumns._ID;
    public static final String C_CAPTEUR = "capteur";
    public static final String C_IDPLANTE = "idPlante";
    public static final String C_VALEUR = "valeur";

    public BD_Histo_Capteurs(Context context) {
        this.context = context;
        dbHelper = new BD_Histo_Capteurs.DBHelper();
    }

    public void insert (String capteur, String idPlante, int valeur){
        ContentValues fieldsValues = new ContentValues();
        db = dbHelper.getWritableDatabase();
        fieldsValues.put(C_CAPTEUR, capteur);
        fieldsValues.put(C_IDPLANTE, idPlante);
        fieldsValues.put(C_VALEUR, valeur);

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

            sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, C_ID, C_CAPTEUR ,C_IDPLANTE, C_VALEUR);

            db.execSQL(sql);
            Log.d(TAG, String.format("onCreate(): La table %s a été créée avec succès.", TABLE_NAME));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
