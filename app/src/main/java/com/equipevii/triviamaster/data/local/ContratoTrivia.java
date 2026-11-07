package com.equipevii.triviamaster.data.local;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ContratoTrivia {
    private ContratoTrivia(){}
    public static final String AUTHORITY = "com.equipevii.triviamaster.triviaprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class TabelaPartida implements BaseColumns {
        public static final String TABELA_NOME = "match";
        public static final String COLUNA_JOGADOR = "match_player";
        public static final String COLUNA_CATEGORIA = "match_category";
        public static final String COLUNA_DIFICULDADE = "match_difficulty";
        public static final String COLUNA_ACERTOS = "correct_guesses";
        public static final String COLUNA_TOTAL = "total_guesses";
        public static final String COLUNA_PONTUACAO = "match_score";
        public static final String COLUNA_DATA = "match_time";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                "matches");
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE: " + TabelaPartida.TABELA_NOME + " (" + TabelaPartida._ID +
            " INTEGER PRIMARY KEY," + TabelaPartida.COLUNA_JOGADOR
            + " TEXT," + TabelaPartida.COLUNA_CATEGORIA + " TEXT," +
            TabelaPartida.COLUNA_DIFICULDADE + " TEXT," + TabelaPartida.COLUNA_ACERTOS
            + " INTEGER," + TabelaPartida.COLUNA_TOTAL + " INTEGER," +
            TabelaPartida.COLUNA_PONTUACAO + " INTEGER," + TabelaPartida.COLUNA_DATA +
            " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TabelaPartida.TABELA_NOME;
}
