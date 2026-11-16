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
    private static final int PARTIDAS = 101;
    private static final int PARTIDAS_ID = 102;
    private static final int QUESTOES = 201;
    private static final int QUESTOES_ID = 202;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "partidas", PARTIDAS);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "partidas/#", PARTIDAS_ID);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "questoes", QUESTOES);
        uriMatcher.addURI(ContratoTrivia.AUTHORITY, "questoes/#", QUESTOES_ID);
    }

    private BancoTriviaHelper dbHelper;

    @Override
    public boolean onCreate(){
        Context context = getContext();
        dbHelper = new BancoTriviaHelper(context);
        return true;
    }

    private SQLiteDatabase banco(){
        return dbHelper.getWritableDatabase();
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
                cursor = banco().query(ContratoTrivia.RegistroPartida.TABELA_NOME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case PARTIDAS_ID:
                cursor = banco().query(ContratoTrivia.RegistroPartida.TABELA_NOME, projection,
                        ContratoTrivia.RegistroPartida._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null, null, sortOrder);
                break;
            case QUESTOES:
                cursor = banco().query(ContratoTrivia.RegistroQuestao.TABELA_NOME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case QUESTOES_ID:
                cursor = banco().query(ContratoTrivia.RegistroQuestao.TABELA_NOME, projection,
                        ContratoTrivia.RegistroQuestao._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
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
                rowId = banco().insert(ContratoTrivia.RegistroPartida.TABELA_NOME, null, values);
                if(rowId > 0){
                    newUri = ContentUris.withAppendedId(ContratoTrivia.RegistroPartida.CONTENT_URI, rowId);
                }
                else{
                    throw new SQLiteException("Erro ao adicionar valores em " + uri);
                }
                break;
            case QUESTOES:
                rowId = banco().insertWithOnConflict(ContratoTrivia.RegistroQuestao.TABELA_NOME,
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
        int removedRows;
        switch(uriMatcher.match(uri)){
            case PARTIDAS:
                removedRows = banco().delete(ContratoTrivia.RegistroPartida.TABELA_NOME, selection,
                        selectionArgs);
                break;
            case QUESTOES:
                removedRows = banco().delete(ContratoTrivia.RegistroQuestao.TABELA_NOME, selection,
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

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs){
        int updatedRows;
        if(uriMatcher.match(uri) == QUESTOES){
            updatedRows = banco().update(ContratoTrivia.RegistroQuestao.TABELA_NOME,
                    values, selection, selectionArgs);
            if (updatedRows > 0 && getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return updatedRows;
        }
        throw new SecurityException("URI não permitida: " + uri);
    }
}

/*
 * Alterações (Mylena):
 * - Corrigido "CREATE TABLE:" para "CREATE TABLE" (os dois-pontos davam erro
 *   de SQL e o app fechava ao abrir o banco).
 * - Criados SQL_CREATE_QUESTOES e SQL_DELETE_QUESTOES: a tabela "questao"
 *   não era criada, então o cache offline não tinha onde salvar.
 * - Nova coluna COLUNA_ID_CATEGORIA: guarda o id da categoria (ex: "18"),
 *   porque a API só devolve o nome e o cache precisa filtrar pelo id.
 * - UNIQUE no enunciado para a mesma questão não ser salva duas vezes.
 */