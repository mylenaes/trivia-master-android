package com.equipevii.triviamaster.data.local;
import android.content.Context;
import android.database.sqlite.*;

public class BancoTriviaHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TriviaMatches.db";

    public BancoTriviaHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(ContratoTrivia.SQL_CREATE_ENTRIES);
        db.execSQL(ContratoTrivia.SQL_CREATE_QUESTOES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(ContratoTrivia.SQL_DELETE_ENTRIES);
        db.execSQL(ContratoTrivia.SQL_DELETE_QUESTOES);
        onCreate(db);
    }
}

/*
 * Alterações (Mylena):
 * - Adicionada a tabela "questao" no onCreate e no onUpgrade.
 *   Ela guarda as questões que vêm da API, para o app jogar sem internet.
 * - DATABASE_VERSION mudou de 1 para 2, para o onUpgrade recriar as
 *   tabelas em quem já tinha o app instalado (o histórico antigo é apagado).
 */