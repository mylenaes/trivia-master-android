package com.equipevii.triviamaster.data.local;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ContratoTrivia {
    private ContratoTrivia(){}
    public static final String AUTHORITY = "com.equipevii.triviamaster.triviaprovider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class RegistroPartida implements BaseColumns {
        public static final String TABELA_NOME = "partida";
        public static final String COLUNA_JOGADOR = "partida_jogador";
        public static final String COLUNA_CATEGORIA = "partida_categoria";
        public static final String COLUNA_DIFICULDADE = "partida_dificuldade";
        public static final String COLUNA_ACERTOS = "partida_acertos";
        public static final String COLUNA_TOTAL = "partida_total";
        public static final String COLUNA_PONTUACAO = "partida_pontuacao";
        public static final String COLUNA_DATA = "partida_data";
        public static final String COLUNA_HORARIO = "partida_horario";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                "partidas");
    }

    public static final class RegistroQuestao implements BaseColumns {
        public static final String TABELA_NOME = "questao";
        public static final String COLUNA_ID_CATEGORIA = "questao_id_categoria";
        public static final String COLUNA_CATEGORIA = "questao_categoria";
        public static final String COLUNA_DIFICULDADE = "questao_dificuldade";
        public static final String COLUNA_ENUNCIADO = "questao_enunciado";
        public static final String COLUNA_RESPOSTA_CORRETA = "resposta_correta";
        public static final String COLUNA_RESPOSTAS_ERRADAS = "respostas_erradas";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                "questoes");
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RegistroPartida.TABELA_NOME + " (" + RegistroPartida._ID +
                    " INTEGER PRIMARY KEY," + RegistroPartida.COLUNA_JOGADOR
                    + " TEXT," + RegistroPartida.COLUNA_CATEGORIA + " TEXT," +
                    RegistroPartida.COLUNA_DIFICULDADE + " TEXT," + RegistroPartida.COLUNA_ACERTOS
                    + " INTEGER," + RegistroPartida.COLUNA_TOTAL + " INTEGER," +
                    RegistroPartida.COLUNA_PONTUACAO + " INTEGER," + RegistroPartida.COLUNA_DATA +
                    " TEXT," + RegistroPartida.COLUNA_HORARIO + " TEXT)";

    public static final String SQL_CREATE_QUESTOES =
            "CREATE TABLE " + RegistroQuestao.TABELA_NOME + " (" + RegistroQuestao._ID +
                    " INTEGER PRIMARY KEY," + RegistroQuestao.COLUNA_ID_CATEGORIA + " TEXT," +
                    RegistroQuestao.COLUNA_CATEGORIA + " TEXT," +
                    RegistroQuestao.COLUNA_DIFICULDADE + " TEXT," +
                    RegistroQuestao.COLUNA_ENUNCIADO + " TEXT UNIQUE," +
                    RegistroQuestao.COLUNA_RESPOSTA_CORRETA + " TEXT," +
                    RegistroQuestao.COLUNA_RESPOSTAS_ERRADAS + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RegistroPartida.TABELA_NOME;

    public static final String SQL_DELETE_QUESTOES =
            "DROP TABLE IF EXISTS " + RegistroQuestao.TABELA_NOME;
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