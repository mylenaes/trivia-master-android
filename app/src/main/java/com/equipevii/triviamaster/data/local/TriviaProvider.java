package com.equipevii.triviamaster.data.local;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class TriviaProvider extends ContentProvider{
    private static final int PARTIDAS_TODAS = 1;
    private static final int PARTIDAS_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "matches", PARTIDAS_TODAS);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "matches/*", PARTIDAS_ID);
    }

    private BancoTriviaHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public boolean onCreate(){
        Context context = getContext();
        dbHelper = new BancoTriviaHelper(context);
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Override
    public String getType(Uri uri){
        switch(uriMatcher.match(uri)){
            case PARTIDAS_TODAS:
                return "vnd.android.cursor.dir/matches";
            case PARTIDAS_ID:
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
            case PARTIDAS_TODAS:
                cursor = db.query(ContratoTrivia.TabelaPartida.TABELA_NOME, null,
                        null, null, null, null, sortOrder);
                break;
            case PARTIDAS_ID:
                cursor = db.query(ContratoTrivia.TabelaPartida.TABELA_NOME, null,
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
        long rowId = db.insert(ContratoTrivia.TabelaPartida.TABELA_NOME, null, values);
        if(rowId > 0){
            Uri newUri = ContentUris.withAppendedId(ContratoTrivia.TabelaPartida.CONTENT_URI, rowId);
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
        removedRows = db.delete(ContratoTrivia.TabelaPartida.TABELA_NOME, selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return removedRows;
    }

    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs){
        throw new SecurityException("Operação de atualização não permitida.");
    }
}
