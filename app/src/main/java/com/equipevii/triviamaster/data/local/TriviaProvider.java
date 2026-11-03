package com.equipevii.triviamaster.data.local;
import com.equipevii.triviamaster.model.*;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import java.util.List;

public class TriviaProvider extends ContentProvider{
    private static final int MATCHES_ALL = 1;
    private static final int MATCHES_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(TriviaContract.AUTHORITY, "matches", MATCHES_ALL);
        uriMatcher.addURI(TriviaContract.AUTHORITY, "matches/*", MATCHES_ID);
    }

    private TriviaDbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate(){
        Context context = getContext();
        dbHelper = new TriviaDbHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public String getType(Uri uri){
        switch(uriMatcher.match(uri)){
            case MATCHES_ALL:
                return "vnd.android.cursor.dir/matches";
            case MATCHES_ID:
                return "vnd.android.cursor.item/matches";
            default:
                throw new IllegalArgumentException("URI desconhecida: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        Cursor cursor;
        switch(uriMatcher.match(uri)){
            case MATCHES_ALL:
                cursor = db.query(TriviaContract.MatchEntry.TABLE_NAME, null,
                        null, null, null, null, sortOrder);
                break;
            case MATCHES_ID:
                cursor = db.query(TriviaContract.MatchEntry.TABLE_NAME, null,
                        selection, new String[] {String.valueOf(selection)},
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI não suportada: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        long rowId = db.insert(TriviaContract.MatchEntry.TABLE_NAME, null, values);
        if(rowId > 0){
            Uri newUri = ContentUris.withAppendedId(TriviaContract.MatchEntry.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLiteException("Erro ao adicionar valores em " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int removedRows = 0;
        if(uriMatcher.match(uri) > 2){
            throw new IllegalArgumentException("URI desconhecida: " + uri);
        }
        removedRows = db.delete(TriviaContract.MatchEntry.TABLE_NAME, selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return removedRows;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs){
        throw new SecurityException("Operação de atualização não permitida.");
    }
}
