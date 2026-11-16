package com.equipevii.triviamaster.data.repositorio;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import com.equipevii.triviamaster.data.local.ContratoTrivia.RegistroPartida;
import com.equipevii.triviamaster.data.local.ContratoTrivia.RegistroQuestao;
import com.equipevii.triviamaster.data.remoto.ClienteApiTrivia;
import com.equipevii.triviamaster.model.ConfigPartida;
import com.equipevii.triviamaster.model.Partida;
import com.equipevii.triviamaster.model.Questao;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RepositorioTriviaImpl implements TriviaRepository {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final ClienteApiTrivia api = new ClienteApiTrivia();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ContentResolver resolver;

    public RepositorioTriviaImpl(Context context) {
        // applicationContext para não segurar a Activity na memória
        this.resolver = context.getApplicationContext().getContentResolver();
    }

    @Override
    public void getQuestions(ConfigPartida config, Callback<List<Questao>> callback) {
        if (config == null) {
            entregarErro(callback, "Configurações da partida não encontradas.");
            return;
        }
        executor.execute(() -> {
            try {
                List<Questao> questoes = api.buscarQuestoes(config);
                salvarNoCache(questoes, config);
                entregarSucesso(callback, questoes);

            } catch (Exception erroApi) {
                List<Questao> salvas = buscarNoCache(config);

                if (salvas.size() >= config.getQuantidade()) {
                    entregarSucesso(callback, salvas);
                } else {
                    entregarErro(callback, mensagemAmigavel(erroApi)
                            + " Não há questões salvas suficientes para jogar offline.");
                }
            }
        });
    }
    public void atualizarCache(ConfigPartida config, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                List<Questao> questoes = api.buscarQuestoes(config);
                int salvas = salvarNoCache(questoes, config);
                entregarSucesso(callback, salvas);
            } catch (Exception e) {
                entregarErro(callback, mensagemAmigavel(e));
            }
        });
    }

    @Override
    public void saveMatch(Partida partida, Callback<Long> callback) {
        executor.execute(() -> {
            try {
                ContentValues valores = new ContentValues();
                valores.put(RegistroPartida.COLUNA_JOGADOR, partida.getJogador());
                valores.put(RegistroPartida.COLUNA_CATEGORIA, partida.getCategoria());
                valores.put(RegistroPartida.COLUNA_DIFICULDADE, partida.getDificuldade());
                valores.put(RegistroPartida.COLUNA_ACERTOS, partida.getAcertos());
                valores.put(RegistroPartida.COLUNA_TOTAL, partida.getTotal_questoes());
                valores.put(RegistroPartida.COLUNA_PONTUACAO, partida.getPontuacao());
                valores.put(RegistroPartida.COLUNA_DATA, partida.getData());
                valores.put(RegistroPartida.COLUNA_HORARIO, partida.getHorario());
                Uri novaUri = resolver.insert(RegistroPartida.CONTENT_URI, valores);
                entregarSucesso(callback, ContentUris.parseId(novaUri));
            } catch (Exception e) {
                entregarErro(callback, "Não foi possível salvar a partida.");
            }
        });
    }

    @Override
    public void getHistory(Callback<List<Partida>> callback) {
        executor.execute(() -> {
            List<Partida> partidas = new ArrayList<>();
            try (Cursor cursor = resolver.query(RegistroPartida.CONTENT_URI, null,
                    null, null, RegistroPartida._ID + " DESC")) {
                while (cursor != null && cursor.moveToNext()) {
                    partidas.add(lerPartida(cursor));
                }
                entregarSucesso(callback, partidas);
            } catch (Exception e) {
                entregarErro(callback, "Não foi possível carregar o histórico.");
            }
        });
    }
    private int salvarNoCache(List<Questao> questoes, ConfigPartida config) {
        int salvas = 0;
        for (Questao questao : questoes) {
            try {
                ContentValues valores = new ContentValues();
                valores.put(RegistroQuestao.COLUNA_ID_CATEGORIA, textoOuVazio(config.getCategoria()));
                valores.put(RegistroQuestao.COLUNA_CATEGORIA, questao.getCategoria());
                valores.put(RegistroQuestao.COLUNA_DIFICULDADE, questao.getDificuldade());
                valores.put(RegistroQuestao.COLUNA_ENUNCIADO, questao.getEnunciado());
                valores.put(RegistroQuestao.COLUNA_RESPOSTA_CORRETA, questao.getRespostaCorreta());
                valores.put(RegistroQuestao.COLUNA_RESPOSTAS_ERRADAS,
                        new JSONArray(Arrays.asList(questao.getRespostasErradas())).toString());
                resolver.insert(RegistroQuestao.CONTENT_URI, valores);
                salvas++;
            } catch (Exception e) {
                //ignora só essa questão e segue para a próxima
            }
        }
        return salvas;
    }

    private List<Questao> buscarNoCache(ConfigPartida config) {
        List<Questao> questoes = new ArrayList<>();
        List<String> argumentos = new ArrayList<>();
        StringBuilder filtro = new StringBuilder();
        String categoria = config.getCategoria();
        if (categoria != null && !categoria.isEmpty()) {
            filtro.append(RegistroQuestao.COLUNA_ID_CATEGORIA).append(" = ?");
            argumentos.add(categoria);
        }
        String dificuldade = config.getDificuldade();
        if (dificuldade != null && !dificuldade.isEmpty()) {
            if (filtro.length() > 0) {
                filtro.append(" AND ");
            }
            filtro.append(RegistroQuestao.COLUNA_DIFICULDADE).append(" = ?");
            argumentos.add(dificuldade);
        }
        String selecao = filtro.length() > 0 ? filtro.toString() : null;
        try (Cursor cursor = resolver.query(RegistroQuestao.CONTENT_URI, null, selecao,
                argumentos.toArray(new String[0]), "RANDOM()")) {
            while (cursor != null && cursor.moveToNext()
                    && questoes.size() < config.getQuantidade()) {
                questoes.add(lerQuestao(cursor));
            }
        } catch (Exception e) {
            // se o banco falha, devolve lista vazia e o erro aparece para o user
        }
        return questoes;
    }

    private Questao lerQuestao(Cursor cursor) throws JSONException {
        JSONArray arrayErradas = new JSONArray(cursor.getString(
                cursor.getColumnIndexOrThrow(RegistroQuestao.COLUNA_RESPOSTAS_ERRADAS)));
        String[] erradas = new String[arrayErradas.length()];
        for (int i = 0; i < arrayErradas.length(); i++) {
            erradas[i] = arrayErradas.getString(i);
        }
        return new Questao(
                cursor.getInt(cursor.getColumnIndexOrThrow(RegistroQuestao._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroQuestao.COLUNA_CATEGORIA)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroQuestao.COLUNA_DIFICULDADE)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroQuestao.COLUNA_ENUNCIADO)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroQuestao.COLUNA_RESPOSTA_CORRETA)),
                erradas
        );
    }

    private Partida lerPartida(Cursor cursor) {
        return new Partida(
                cursor.getInt(cursor.getColumnIndexOrThrow(RegistroPartida._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_JOGADOR)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_CATEGORIA)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_DIFICULDADE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_ACERTOS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_TOTAL)),
                cursor.getInt(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_PONTUACAO)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_DATA)),
                cursor.getString(cursor.getColumnIndexOrThrow(RegistroPartida.COLUNA_HORARIO))
        );
    }

    private String mensagemAmigavel(Exception e) {
        String mensagem = e.getMessage();
        if (!(e instanceof IOException) || mensagem == null || mensagem.isEmpty()) {
            return "Não foi possível buscar as questões.";
        }
        return mensagem;
    }

    private String textoOuVazio(String texto) {
        return texto == null ? "" : texto;
    }
    private <T> void entregarSucesso(Callback<T> callback, T dado) {
        handler.post(() -> callback.onSuccess(dado));
    }
    private <T> void entregarErro(Callback<T> callback, String mensagem) {
        handler.post(() -> callback.onError(mensagem));
    }
}