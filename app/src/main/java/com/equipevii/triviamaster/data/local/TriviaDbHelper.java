package com.equipevii.triviamaster.data.local;
import android.content.Context;
import android.database.sqlite.*;

public class TriviaDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TriviaMatches.db";

    public TriviaDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(TriviaContract.SQL_CREATE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(TriviaContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
