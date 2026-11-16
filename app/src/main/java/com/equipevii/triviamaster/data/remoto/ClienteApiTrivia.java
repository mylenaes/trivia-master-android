package com.equipevii.triviamaster.data.remoto;

import android.text.Html;
import com.equipevii.triviamaster.model.ConfigPartida;
import com.equipevii.triviamaster.model.Questao;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ClienteApiTrivia {
    private static final String URL_BASE = "https://opentdb.com/api.php";

    public List<Questao> buscarQuestoes(ConfigPartida config) throws IOException {
        String url = URL_BASE + "?amount=" + config.getQuantidade() + "&type=multiple";
        // categoria e dificuldade vazias = a API escolhe qualquer uma
        if (config.getCategoria() != null && !config.getCategoria().isEmpty()) {
            url += "&category=" + config.getCategoria();
        }
        if (config.getDificuldade() != null && !config.getDificuldade().isEmpty()) {
            url += "&difficulty=" + config.getDificuldade();
        }

        int codigoHttp = -1;
        StringBuilder json = new StringBuilder();
        HttpURLConnection conexao = null;

        try {
            conexao = (HttpURLConnection) new URL(url).openConnection();
            conexao.setConnectTimeout(10000);
            conexao.setReadTimeout(10000);
            codigoHttp = conexao.getResponseCode();
            if (codigoHttp == HttpURLConnection.HTTP_OK) {
                BufferedReader leitor = new BufferedReader(
                        new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8));
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    json.append(linha);
                }
                leitor.close();
            }

        } catch (SocketTimeoutException e) {
            throw new IOException("A conexão demorou demais. Tente de novo.", e);
        } catch (UnknownHostException e) {
            throw new IOException("Sem conexao com a internet.", e);
        } catch (IOException e) {
            throw new IOException("Não foi possível conectar ao servidor.", e);
        } finally {
            if (conexao != null) {
                conexao.disconnect();
            }
        }
        if (codigoHttp != HttpURLConnection.HTTP_OK) {
            throw new IOException("O servidor de perguntas não responde. Tente novamente em alguns segundos.");
        }

        List<Questao> questoes = new ArrayList<>();

        try {
            JSONObject raiz = new JSONObject(json.toString());
            int codigo = raiz.getInt("response_code");
            if (codigo == 1) {
                throw new IOException("Não há perguntas suficientes para essa configuracão. "
                        + "Tente diminuir a quantidade ou troca a categoria.");
            }
            if (codigo != 0) {
                throw new IOException("Não foi possível carregar as perguntas (código " + codigo
                        + "). Tente novamente em alguns segundos.");
            }
            JSONArray resultados = raiz.getJSONArray("results");
            for (int i = 0; i < resultados.length(); i++) {
                JSONObject item = resultados.getJSONObject(i);
                JSONArray arrayErradas = item.getJSONArray("incorrect_answers");
                String[] erradas = new String[arrayErradas.length()];
                for (int j = 0; j < erradas.length; j++) {
                    erradas[j] = decodificar(arrayErradas.getString(j));
                }
                questoes.add(new Questao(
                        0,
                        decodificar(item.getString("category")),
                        item.getString("difficulty"),
                        decodificar(item.getString("question")),
                        decodificar(item.getString("correct_answer")),
                        erradas
                ));
            }

        } catch (JSONException e) {
            throw new IOException("A resposta do servidor veio com problema.", e);
        }
        return questoes;
    }

    private String decodificar(String texto) {
        return Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY).toString();
    }
}
