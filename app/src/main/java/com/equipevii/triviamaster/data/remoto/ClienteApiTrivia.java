package com.equipevii.triviamaster.data.remoto;

import android.text.Html;
import com.equipevii.triviamaster.model.Questao;
import com.equipevii.triviamaster.model.ConfigPartida;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
    private static final Object TRAVA = new Object();

    //coordena a busca de questoes na API
    //recebe o objeto ConfigPartida com as configuracoes escolhidas pelo jogador
    public List<Questao> buscarQuestoes(ConfigPartida config) throws IOException, JSONException {
        synchronized (TRAVA) {
            String url = montarURL(config);
            try {
                String resposta = requisicaoGet(url);
                return interpretarResposta(resposta);
            } catch (SocketTimeoutException e) {
                throw new IOException("Tempo de conexão esgotado!", e);
            } catch (UnknownHostException e) {
                throw new IOException("Sem conexão com a internet!", e);
            }
        }
    }

    //monta dinamicamente a URL que será utilizada pra consultar a API
    private String montarURL(ConfigPartida config) {
        StringBuilder construtor = new StringBuilder(URL_BASE);
        construtor.append("?amount=").append(config.getQuantidade());
        construtor.append("&type=multiple");

        if (config.getCategoria() > 0) {
            construtor.append("&category=").append(config.getCategoria());
        }
        String dificuldade = config.getDificuldade();

        if (dificuldade != null && !dificuldade.isEmpty()) {
            construtor.append("&difficulty=").append(dificuldade);
        }
        return construtor.toString();
    }

    //faz a conexao com o Open Trivia db e recebe a resposta do servidor
    private String requisicaoGet(String enderecoUrl) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) new URL(enderecoUrl).openConnection();

        try {
            conexao.setRequestMethod("GET");
            conexao.setConnectTimeout(10000);
            conexao.setReadTimeout(10000);
            int codigoHttp = conexao.getResponseCode();

            if (codigoHttp != HttpURLConnection.HTTP_OK) {
                throw new IOException("Erro na comunicacao com o servidor!");
            }
            return lerFluxo(conexao.getInputStream());
        } finally {
            conexao.disconnect();
        }
    }

    //metodo que le os dados recebidos da API e transforma o conteudo em uma string contendo o JSON
    private String lerFluxo(InputStream entrada) throws IOException {
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            StringBuilder texto = new StringBuilder();
            String linha;

            while ((linha = leitor.readLine()) != null) {
                texto.append(linha);
            }
            return texto.toString();
        }
    }

    //interpreta o JSON recebido da API e transforma cada pergunta em um objeto java da classe Questao
    private List<Questao> interpretarResposta(String corpo) throws JSONException, IOException {
        JSONObject raiz = new JSONObject(corpo);
        int codigoResposta = raiz.getInt("response_code");

        if (codigoResposta != 0) {
            throw new IOException("Não foi possivel obter questoes da API! (codigo " + codigoResposta + ")");
        }
        JSONArray resultados = raiz.getJSONArray("results");
        List<Questao> questoes = new ArrayList<>();

        for (int i = 0; i < resultados.length(); i++) {
            JSONObject item = resultados.getJSONObject(i);

            String categoria = decodificar(item.getString("category"));
            String dificuldade = decodificar(item.getString("difficulty"));
            String enunciado = decodificar(item.getString("question"));
            String correta = decodificar(item.getString("correct_answer"));

            List<String> erradas = new ArrayList<>();

            JSONArray arrayErradas = item.getJSONArray("incorrect_answers");

            for (int j = 0; j < arrayErradas.length(); j++) {
                erradas.add(decodificar(arrayErradas.getString(j)));
            }
            questoes.add(new Questao(
                    categoria,
                    dificuldade,
                    enunciado,
                    correta,
                    erradas
            ));
        }
        return questoes;
    }

    //Converte entidades HTML em caracteres normais
    private String decodificar(String texto) {
        return Html.fromHtml(
                texto,
                Html.FROM_HTML_MODE_LEGACY
        ).toString();
    }
}