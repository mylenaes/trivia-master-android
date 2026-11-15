package com.equipevii.triviamaster.ui.quiz;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.equipevii.triviamaster.ui.model.Questao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.os.CountDownTimer;

//uma view model pode manter os dados da partida separados da activity
public class QuizViewModel extends ViewModel {
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

    //inicializar view model
    public QuizViewModel(){
        criarListaQuestoes();
        pontuacaoLiveData.setValue(0);
        prepararQuestaoAtual();
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

    private void criarListaQuestoes(){
        listaQuestoes = new ArrayList<>();

        listaQuestoes.add(new Questao(
                1,
                "Computadores",
                "Médio",
                "Qual linguagem é usada no Android Studio além de Java?",
                "Kotlin",
                Arrays.asList("Swift", "Ruby", "PHP")
        ));
        listaQuestoes.add(new Questao(
                2,
                "Computadores",
                "Médio",
                "Quantos bits tem um byte?",
                "8",
                Arrays.asList("4", "16", "32")
        ));
        listaQuestoes.add(new Questao(
                3,
                "Computadores",
                "Médio",
                "Qual porta padrão do HTTPS?",
                "443",
                Arrays.asList("80", "21", "8080")
        ));
        listaQuestoes.add(new Questao(
                4,
                "Computadores",
                "Médio",
                "Qual empresa criou o Android?",
                "Google",
                Arrays.asList("Apple", "Microsoft", "Samsung")
        ));
        listaQuestoes.add(new Questao(
                5,
                "Computadores",
                "Médio",
                "O que significa CPU?",
                "Central Processing Unit",
                Arrays.asList("Computer Power Unit", "Core Program Utility", "Central Print Unit")
        ));
    }

    private void prepararQuestaoAtual(){
        //pegar questão atual
        Questao questao = listaQuestoes.get(indiceQuestaotual);
        opcaoCorreta = questao.getRespostaCorreta();
        respondeu = false;
        //pegar as alternativas da questão atual
        List<String> alternativas = new ArrayList<>(
                questao.getRespostasErradas()
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
        //avancar somente se não estiver na ultima questao
        if(indiceQuestaotual < listaQuestoes.size() - 1){
            indiceQuestaotual++;
            prepararQuestaoAtual();
            return true;
        }

        return false;
    }

    public void verificarResposta(String respostaEscolhida){
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
            case "fácil":
                return 1;
            case "médio":
                return 2;
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
