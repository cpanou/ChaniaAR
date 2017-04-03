package tuc.christos.chaniacitywalk2.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import tuc.christos.chaniacitywalk2.model.Scene;

/**
 * Created by Christos on 16-Feb-17.
 *
 */

final class mDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "myDBHelper";
    private static final int DB_VERSION = 1;
    private static String DB_PATH;// = "/data/data/tuc.christos.chaniacitywalk2/databases/";
    private static String DB_NAME = "scenesDBtest.db";
    private static String DB_NAME_c = "ARAppDB.db";
    private SQLiteDatabase myDataBase;

    private final Context mContext;

    /*private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SceneEntry.TABLE_NAME;

    /*private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS" + SceneEntry.TABLE_NAME + " (" +
                    SceneEntry._ID + " INTEGER PRIMARY KEY," +
                    SceneEntry.TABLE_COLUMN_NAME + " TEXT," +
                    SceneEntry.TABLE_COLUMN_TAG + " TEXT,"+
                    SceneEntry.TABLE_COLUMN_DESCRIPTION + "TEXT,"+
                    SceneEntry.TABLE_COLUMN_LATITUDE + "REAL,"+
                    SceneEntry.TABLE_COLUMN_LONGITUDE + "REAL,"+
                    SceneEntry.TABLE_COLUMN_UNLOCKED + "BOOLEAN,"+
                    SceneEntry.TABLE_COLUMN_VISIBLE + "BOOLEAN,"+
                    SceneEntry.TABLE_COLUMN_HASAR + "BOOLEAN)";
    */

    mDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.i(TAG,"ERROR COPYING DATABASE");
                throw new Error("Error copying database");
            }
        }

    }

    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
            Log.i(TAG,"database doesn't exist yet");
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    void closeDataBase(){
        myDataBase.close();
    }
	
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // onUpgrade(db, oldVersion, newVersion);
    }

    Cursor getEntries(){

        String selectQ = "SELECT * FROM Scenes";

        //return myDataBase.query(SceneEntry.TABLE_NAME, projection,null,null,null,null,null);
        return myDataBase.rawQuery(selectQ,null);
    }

    void updateLocalDB(ArrayList<Scene> Scenes){

        String deleteQ = "DELETE * FROM Scenes";
        myDataBase.rawQuery(deleteQ, null);

        String insertQ = "INSERT INTO Scenes (" + SceneEntry._ID + ","
                + SceneEntry.TABLE_COLUMN_NAME + ","
                + SceneEntry.TABLE_COLUMN_LATITUDE + ","
                + SceneEntry.TABLE_COLUMN_LONGITUDE + ","
                + SceneEntry.TABLE_COLUMN_VISITED + ","
                + SceneEntry.TABLE_COLUMN_VISIBLE + ","
                + SceneEntry.TABLE_COLUMN_HASAR + ","
                + SceneEntry.TABLE_COLUMN_DESCRIPTION + ","
                + SceneEntry.TABLE_COLUMN_TAG + ")";

        for (Scene temp: Scenes) {

            String valuesQ =  "VALUES (" + temp.getId() +","
                    + temp.getName() + ","
                    + temp.getLatitude() + ","
                    + temp.getLongitude() + ","
                    + temp.isVisited() + ","
                    + temp.isVisible() + ","
                    + temp.isHasAR() + ","
                    + temp.getBriefDesc() + ","
                    + temp.getTAG() + ")";
            myDataBase.rawQuery( insertQ+valuesQ , null);
        }
    }

    static class SceneEntry implements BaseColumns{
        //public static final String TABLE_NAME="Scenes";
        static final String TABLE_COLUMN_NAME="name";
        static final String TABLE_COLUMN_LATITUDE="latitude";
        static final String TABLE_COLUMN_LONGITUDE="longitude";
        static final String TABLE_COLUMN_VISITED="visited";
        static final String TABLE_COLUMN_VISIBLE="visible";
        static final String TABLE_COLUMN_HASAR="hasAR";
        static final String TABLE_COLUMN_DESCRIPTION="description";
        static final String TABLE_COLUMN_TAG="TAG";
    }

}
