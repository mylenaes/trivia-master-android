package com.equipevii.triviamaster.ui.quiz;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.equipevii.triviamaster.data.repositorio.RepositorioTriviaImpl;
import com.equipevii.triviamaster.data.repositorio.TriviaRepository;
import com.equipevii.triviamaster.model.ConfigPartida;
import com.equipevii.triviamaster.model.Partida;
import com.equipevii.triviamaster.model.Questao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.CountDownTimer;

//uma view model pode manter os dados da partida separados da activity
public class QuizViewModel extends AndroidViewModel {
    private List<Questao> listaQuestoes;
    private int indiceQuestaotual = 0;
    private List<String> alternativasAtuais;
    private String opcaoCorreta;
    private boolean respondeu = false;
    private int pontuacaoAtual = 0;

    private static final long TEMPO_TOTAL = 20000; //2000ms = 20s
    private static final long INTERVALO = 1000; //1000ms = 1s

    private CountDownTimer countDownTimer; //objeto timer
    private long tempoRestante = TEMPO_TOTAL;

    //o live data é usado para que a activity possa ser avisada se os dados mudarem
    //os mutable podem ser alterados, os live data só podem ser observados
    private MutableLiveData<Questao> questaoAtual = new MutableLiveData<>();
    private MutableLiveData<List<String>> alternativasAtuaisLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> progresso = new MutableLiveData<>();
    private MutableLiveData<Integer> numeroQuestao = new MutableLiveData<>();
    private MutableLiveData<Boolean> respostaCorreta = new MutableLiveData<>(); //se a resposta é certa ou errada
    private MutableLiveData<Integer> pontuacaoLiveData = new MutableLiveData<>();
    private MutableLiveData<Long> tempoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> tempoEsgotado = new MutableLiveData<>(); //se o tempo esgotou
    private MutableLiveData<String> erro = new MutableLiveData<>(); //mensagem se não conseguir buscar as questões

    private MutableLiveData<Boolean> partidaSalva = new MutableLiveData<>(); //true se salvou, false se deu erro

    private final TriviaRepository repositorio;
    private boolean buscando = false;
    private ConfigPartida configPartida;
    private int acertos = 0;
    private boolean finalizou = false;
    private Partida partidaFinal; //guardada para a tela de resultado

    //inicializar view model
    public QuizViewModel(@NonNull Application application){
        super(application);
        repositorio = new RepositorioTriviaImpl(application);
        pontuacaoLiveData.setValue(0);
    }

    //buscar as questões no repositório (api ou cache)
    public void carregarQuestoes(ConfigPartida config){
        //se já buscou (ex: girou a tela) não busca de novo
        if(listaQuestoes != null || buscando){
            return;
        }
        buscando = true;
        configPartida = config;

        repositorio.getQuestions(config, new TriviaRepository.Callback<List<Questao>>() {
            @Override
            public void onSuccess(List<Questao> questoes) {
                listaQuestoes = questoes;
                prepararQuestaoAtual();
            }

            @Override
            public void onError(String message) {
                buscando = false;
                erro.setValue(message);
            }
        });
    }

    public LiveData<String> getErro(){
        return erro;
    }

    public LiveData<Boolean> getPartidaSalva(){
        return partidaSalva;
    }

    public int getAcertos(){
        return acertos;
    }

    public int getTotalQuestoes(){
        return listaQuestoes == null ? 0 : listaQuestoes.size();
    }

    public int getPontuacaoFinal(){
        return pontuacaoAtual;
    }

    public Partida getPartidaFinal(){
        return partidaFinal;
    }

    //salvar a partida no histórico quando acabar as questões
    public void finalizarPartida(){
        //não deixar salvar duas vezes se clicar rápido
        if(finalizou || listaQuestoes == null){
            return;
        }
        finalizou = true;

        String jogador = configPartida.getNomeJogador();
        if(jogador == null || jogador.isEmpty()){
            jogador = "Jogador";
        }

        //se escolheu qualquer categoria as questões vêm misturadas
        String categoria = "Qualquer categoria";
        if(configPartida.getCategoria() != null && !configPartida.getCategoria().isEmpty()){
            categoria = listaQuestoes.get(0).getCategoria();
        }

        String dificuldade = "Qualquer dificuldade";
        if(configPartida.getDificuldade() != null){
            switch (configPartida.getDificuldade()){
                case "easy":
                    dificuldade = "Fácil";
                    break;
                case "medium":
                    dificuldade = "Médio";
                    break;
                case "hard":
                    dificuldade = "Difícil";
                    break;
            }
        }

        Date agora = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(agora);
        String horario = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(agora);

        Partida partida = new Partida(0, jogador, categoria, dificuldade, acertos,
                listaQuestoes.size(), pontuacaoAtual, data, horario);
        partidaFinal = partida;

        repositorio.saveMatch(partida, new TriviaRepository.Callback<Long>() {
            @Override
            public void onSuccess(Long id) {
                partidaSalva.setValue(true);
            }

            @Override
            public void onError(String message) {
                partidaSalva.setValue(false);
            }
        });
    }

    public LiveData<Questao> getQuestaoAtual(){
        return questaoAtual;
    }

    public LiveData<List<String>> getAlternativasAtuais(){
        return alternativasAtuaisLiveData;
    }

    public LiveData<Integer> getProgresso(){
        return progresso;
    }

    public LiveData<Integer> getNumeroQuestao(){
        return numeroQuestao;
    }

    public LiveData<Boolean> getRespostaCorreta(){
        return respostaCorreta;
    }

    public LiveData<Integer> getPontuacao(){
        return pontuacaoLiveData;
    }

    public LiveData<Long> getTempoRestante(){
        return tempoLiveData;
    }

    public LiveData<Boolean> getTempoEsgotado(){
        return tempoEsgotado;
    }

    private void prepararQuestaoAtual(){
        //pegar questão atual
        Questao questao = listaQuestoes.get(indiceQuestaotual);
        opcaoCorreta = questao.getRespostaCorreta();
        respondeu = false;
        //pegar as alternativas da questão atual
        List<String> alternativas = new ArrayList<>(
                Arrays.asList(questao.getRespostasErradas())
        );
        alternativas.add(questao.getRespostaCorreta());
        //embaralhar alternativas
        Collections.shuffle(alternativas);

        //publicar os dados para a activity
        questaoAtual.setValue(questao);
        alternativasAtuaisLiveData.setValue(alternativas);

        //atualizar progresso
        int valorProgresso = ((indiceQuestaotual + 1) * 100) / listaQuestoes.size();
        progresso.setValue(valorProgresso);

        //atualizar numero da questao
        numeroQuestao.setValue(indiceQuestaotual + 1);

        //preparar pra proxima questão
        respostaCorreta.setValue(null);
        tempoEsgotado.setValue(null);
        iniciarTimer();

    }

    public boolean avancarQuestao(){
        //se as questões ainda não chegaram, não tem para onde avançar
        if(listaQuestoes == null){
            return false;
        }

        //avancar somente se não estiver na ultima questao
        if(indiceQuestaotual < listaQuestoes.size() - 1){
            indiceQuestaotual++;
            prepararQuestaoAtual();
            return true;
        }

        return false;
    }

    public void verificarResposta(String respostaEscolhida){
        //se as questões ainda não chegaram, ignora o clique
        if(listaQuestoes == null){
            return;
        }

        //verificar se já respondeu
        if(respondeu){
            return;
        }

        respondeu = true;

        //parar timer
        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        //verificar acertou
        boolean correta = respostaEscolhida.equals(opcaoCorreta);
        respostaCorreta.setValue(correta);

        if(correta){
            acertos++;
            //pegar questao atual
            Questao questao = listaQuestoes.get(indiceQuestaotual);
            //calcular pontos por dificuldade
            int pontosBase = calcularPontos(questao);
            //calcular bonus de tempo
            int bonus = calcularBonusTempo(pontosBase);
            //atualizar pontuacao total
            pontuacaoAtual += pontosBase + bonus;
            pontuacaoLiveData.setValue(pontuacaoAtual);
        }
    }

    private int calcularPontos(Questao questao){
        String dificuldade = questao.getDificuldade();

        switch (dificuldade.toLowerCase()){
            case "easy": //a api manda a dificuldade em inglês
            case "fácil":
                return 1;
            case "medium":
            case "médio":
                return 2;
            case "hard":
            case "difícil":
                return 3;
            default:
                return 0;
        }
    }

    private int calcularBonusTempo(int pontosBase){
        //pontos base da questão * (tempo restante / tempo total) -> arredondado
        double proporcaoTempo = (double) tempoRestante / TEMPO_TOTAL;
        return (int) Math.round(pontosBase * proporcaoTempo);
    }

    private void iniciarTimer(){
        //se já tiver um timer, cancelar ele
        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        tempoRestante = TEMPO_TOTAL;
        tempoLiveData.setValue(tempoRestante);  //informar tempo inicial

        countDownTimer = new CountDownTimer(TEMPO_TOTAL, INTERVALO) {
            @Override
            //quando terminar o tempo
            public void onFinish() {
                tempoRestante = 0;
                tempoLiveData.setValue(0L);

                //se não conseguiu responder antes
                if(!respondeu){
                    tratarTempoEsgotado();
                }
            }

            @Override
            //a cada segundo
            public void onTick(long l) {
                tempoRestante = l;
                //informar tempo restante
                tempoLiveData.setValue(tempoRestante);
            }
        }.start();
    }

    private void tratarTempoEsgotado(){
        respondeu = true;   //não deixar que responda mais
        tempoEsgotado.setValue(true);   //informar que o tempo esgotou
    }

    protected void onCleared(){
        super.onCleared();

        //cancelar timer se a view model for descartada
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}
