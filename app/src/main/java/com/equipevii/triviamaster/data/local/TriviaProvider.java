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
import androidx.annotation.NonNull;

public class TriviaProvider extends ContentProvider{
    private static final int PARTIDAS = 1;
    private static final int PARTIDAS_ID = 2;

    private static final int QUESTOES = 3;
    private static final int QUESTOES_ID = 4;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "partidas", PARTIDAS);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "partidas/*", PARTIDAS_ID);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "questoes", QUESTOES);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "questoes/*", QUESTOES_ID);
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
    public String getType(@NonNull Uri uri){
        switch(uriMatcher.match(uri)){
            case PARTIDAS:
                return "vnd.android.cursor.dir/partidas";
            case PARTIDAS_ID:
                return "vnd.android.cursor.item/partidas";
            case QUESTOES:
                return "vnd.android.cursor.dir/questoes";
            case QUESTOES_ID:
                return "vnd.android.cursor.item/questoes";
            default:
                throw new IllegalArgumentException("URI desconhecida: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        Cursor cursor;
        switch(uriMatcher.match(uri)){
            case PARTIDAS:
                cursor = db.query(ContratoTrivia.RegistroPartida.TABELA_NOME, null,
                        null, null, null, null, sortOrder);
                break;
            case PARTIDAS_ID:
                cursor = db.query(ContratoTrivia.RegistroPartida.TABELA_NOME, null,
                        selection, new String[] {String.valueOf(selection)},
                        null, null, sortOrder);
                break;
            case QUESTOES:
                cursor = db.query(ContratoTrivia.RegistroQuestao.TABELA_NOME, null,
                        null, null, null, null, sortOrder);
                break;
            case QUESTOES_ID:
                cursor = db.query(ContratoTrivia.RegistroQuestao.TABELA_NOME, null,
                        selection, new String[] {String.valueOf(selection)},
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI não suportada: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values){
        Uri newUri;
        long rowId;

        switch(uriMatcher.match(uri)){
            case PARTIDAS:
                rowId = db.insert(ContratoTrivia.RegistroPartida.TABELA_NOME, null, values);
                if(rowId > 0){
                    newUri = ContentUris.withAppendedId(ContratoTrivia.RegistroPartida.CONTENT_URI, rowId);
                }
                else{
                    throw new SQLiteException("Erro ao adicionar valores em " + uri);
                }
                break;
            case QUESTOES:
                rowId = db.insertWithOnConflict(ContratoTrivia.RegistroQuestao.TABELA_NOME,
                        null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if(rowId > 0){
                    newUri = ContentUris.withAppendedId(ContratoTrivia.RegistroQuestao.CONTENT_URI, rowId);
                }
                else{
                    throw new SQLiteException("Erro ao adicionar valores em " + uri);
                }
                break;
            default:
                throw new SQLiteException("URI desconhecida: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(newUri, null);
        }
        return newUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs){
        int removedRows = 0;
        switch(uriMatcher.match(uri)){
            case PARTIDAS:
                removedRows = db.delete(ContratoTrivia.RegistroPartida.TABELA_NOME, selection,
                        selectionArgs);
                break;
            case QUESTOES:
                removedRows = db.delete(ContratoTrivia.RegistroQuestao.TABELA_NOME, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconhecida: " + uri);
        }
        if (removedRows > 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return removedRows;
    }

    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs){
        int updatedRows = 0;
        if(uriMatcher.match(uri) == QUESTOES){
            updatedRows = db.update(ContratoTrivia.RegistroQuestao.TABELA_NOME,
                    values, selection, selectionArgs);
            if (updatedRows > 0 && getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return updatedRows;
        }
        throw new SecurityException("URI não permitida: " + uri);
    }
}
