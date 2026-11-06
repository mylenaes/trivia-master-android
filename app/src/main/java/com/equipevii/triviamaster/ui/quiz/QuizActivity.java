package com.equipevii.triviamaster.ui.quiz;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.databinding.ActivityQuizBinding;
import com.equipevii.triviamaster.ui.model.Questao;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;    //objeto pra manipular interface
    private LinearLayout opcaoSelecionada;
    private String opcaoCorreta;
    private boolean respondeu = false;
    private List<Questao> listaQuestoes;
    private int indiceQuestaotual = 0;
    private List<String> alternativasAtuais;
    private int pontuacaoAtual = 0;

    private static final long TEMPO_TOTAL = 20000; //20000ms = 20s
    private static final long INTERVALO = 1000; //1000ms = 1s
    private CountDownTimer countDownTimer;   //objeto para contagem regressiva
    private long tempoRestante = TEMPO_TOTAL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater()); //ligação entre java e xml
        setContentView(binding.getRoot());  //definindo layout
        atualizarPontuacao();
        criarListaQuestoes();
        configurarCliqueOpcoes();
        carregarQuestao();
    }

    protected void onDestroy(){
        super.onDestroy();

        //contador para se a tela for destruída
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    private void configurarCliqueOpcoes(){
        binding.opcaoA.setOnClickListener(v ->
                checarResposta(
                        binding.opcaoA,
                        binding.tvOpcaoA.getText().toString()));

        binding.opcaoB.setOnClickListener(v ->
                checarResposta(
                        binding.opcaoB,
                        binding.tvOpcaoB.getText().toString()));

        binding.opcaoC.setOnClickListener(v ->
                checarResposta(
                        binding.opcaoC,
                        binding.tvOpcaoC.getText().toString()));

        binding.opcaoD.setOnClickListener(v ->
                checarResposta(
                        binding.opcaoD,
                        binding.tvOpcaoD.getText().toString()));

        binding.btnProximo.setOnClickListener(v ->{
            //se ainda tiverem questões, passa pra próxima
            if(indiceQuestaotual < listaQuestoes.size() - 1){
                indiceQuestaotual++;
                carregarQuestao();
            } else{ //se chegar oa final das questões diz que acabou
                Toast.makeText(
                        this,
                        "Fim do quiz!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void checarResposta(LinearLayout option, String answer){
        if(respondeu){   //se já respondeu, não pode responder de novo
            return;
        }

        respondeu = true;
        opcaoSelecionada = option;

        //parar o timer quando tiver respondido
        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        boolean correta = answer.equals(opcaoCorreta);   //verificar se tá correto

        if(correta){
            //destaca que a alternativa é correta
            opcaoSelecionada.setBackgroundResource(
              R.drawable.bg_opcao_correta
            );
            getTextoAlternativa(option).setTextColor(Color.parseColor("#1EAB53"));

            //calcular pontuação da questão de acordo com a dificuldade
            Questao questaoAtual = listaQuestoes.get(indiceQuestaotual);
            int pontosBase = calcularPontos(questaoAtual);

            //calcular bônus de tempo e somar aos pontos base
            int bonus = calcularBonusTempo(pontosBase);
            pontuacaoAtual += pontosBase + bonus;

            //atualizar a pontuação
            atualizarPontuacao();

            //exibe feedback
            binding.tvFeedback.setText("Resposta correta!");
            binding.tvFeedback.setTextColor(Color.parseColor("#1EAB53"));
        } else{
            //destaca que a alternativa é incorreta
            opcaoSelecionada.setBackgroundResource(
                    R.drawable.bg_opcao_incorreta
            );
            getTextoAlternativa(option).setTextColor(Color.parseColor("#E23439"));

            //destaca alternativa correta
            destacarRespostaCorreta();

            //exibe feedback
            binding.tvFeedback.setText("Resposta incorreta!");
            binding.tvFeedback.setTextColor(Color.parseColor("#E23439"));
        }

        //tornar feedback visível
        binding.tvFeedback.setVisibility(View.VISIBLE);

        //tornar botão de próximo clicável
        binding.btnProximo.setEnabled(true);
    }

    private TextView getTextoAlternativa(LinearLayout option){
        return (TextView) option.getChildAt(1); //segundo elemento do linearlayout da opção
    }

    private void destacarRespostaCorreta(){
        if(binding.tvOpcaoA.getText().toString().equals(opcaoCorreta)){
            binding.opcaoA.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );
            binding.tvOpcaoA.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOpcaoB.getText().toString().equals(opcaoCorreta)){
            binding.opcaoB.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );
            binding.tvOpcaoB.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOpcaoC.getText().toString().equals(opcaoCorreta)){
            binding.opcaoC.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );

            binding.tvOpcaoC.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOpcaoD.getText().toString().equals(opcaoCorreta)){
            binding.opcaoD.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );

            binding.tvOpcaoD.setTextColor(Color.parseColor("#1EAB53"));
        }
    }

    private void resetarUIQuestao(){
        //reseta variáveis
        respondeu = false;
        opcaoSelecionada = null;

        //reseta alternativas
        binding.opcaoA.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoA.setTextColor(Color.parseColor("#192858"));
        binding.opcaoB.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoB.setTextColor(Color.parseColor("#192858"));
        binding.opcaoC.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoC.setTextColor(Color.parseColor("#192858"));
        binding.opcaoD.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoD.setTextColor(Color.parseColor("#192858"));

        //esconde feedback
        binding.tvFeedback.setVisibility(View.GONE);

        //desativa botão de próximo
        binding.btnProximo.setEnabled(false);
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

    private void carregarQuestao(){
        Questao questaoAtual = listaQuestoes.get(indiceQuestaotual);

        //calcular e atualizar progressbar
        int progresso = ((indiceQuestaotual + 1) * 100) / listaQuestoes.size();
        binding.progressoQuiz.setProgress(progresso);

        //atualiza enunciado
        binding.tvQuestao.setText(questaoAtual.getEnunciado());

        opcaoCorreta = questaoAtual.getRespostaCorreta();

        alternativasAtuais = new ArrayList<>(
                questaoAtual.getRespostasErradas()
        );
        alternativasAtuais.add(opcaoCorreta);

        //embaralhar as opções
        Collections.shuffle(alternativasAtuais);

        //exibir as opções uma em cada textview
        binding.tvOpcaoA.setText(alternativasAtuais.get(0));
        binding.tvOpcaoB.setText(alternativasAtuais.get(1));
        binding.tvOpcaoC.setText(alternativasAtuais.get(2));
        binding.tvOpcaoD.setText(alternativasAtuais.get(3));

        //atualizar numero da questão
        binding.tvNumeroQuestao.setText("Questão: " + (indiceQuestaotual + 1));

        //resetar pra nova questão
        resetarUIQuestao();
        iniciarTimer();
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

    private void atualizarPontuacao(){
        binding.tvPontuacao.setText("Pontos: " + pontuacaoAtual);
    }

    private void iniciarTimer(){
        //cancela timer anterior (se tiver)
        if(countDownTimer != null){
            countDownTimer.cancel();
        }

        //reiniciar tempo
        tempoRestante = TEMPO_TOTAL;

        countDownTimer = new CountDownTimer(TEMPO_TOTAL, INTERVALO) {
            @Override
            //no final do timer
            public void onFinish() {
                tempoRestante = 0;
                binding.tvTimer.setText("0s");
                if(!respondeu){ //se ainda não tiver respondido
                    tratarTempoEsgotado();
                }

            }

            @Override
            //enquanto o tempo passa
            public void onTick(long l) {
                tempoRestante = l;
                int segundos = (int) (l / 1000);    //descobrir quantos segundos falta
                binding.tvTimer.setText(segundos + "s");

                if(segundos > 10){
                    binding.tvTimer.setBackgroundResource(R.drawable.bg_circulo);
                    binding.tvTimer.setTextColor(Color.parseColor("#192858"));
                }

                if(segundos <= 10){
                    binding.tvTimer.setBackgroundResource(R.drawable.bg_circulo_vermelho);
                    binding.tvTimer.setTextColor(Color.parseColor("#FFFFFF"));
                }

            }
        }.start();
    }

    private void tratarTempoEsgotado(){
        //marcar como se tivesse respondido e mostrar resposta correta
        respondeu = true;
        destacarRespostaCorreta();

        //mostrar feedback de tempo esgotado
        binding.tvFeedback.setText("Tempo esgotado!");
        binding.tvFeedback.setTextColor(Color.parseColor("#D93025"));
        binding.tvFeedback.setVisibility(View.VISIBLE);

        //habilitar botão de próximo
        binding.btnProximo.setEnabled(true);
    }

    private int calcularBonusTempo(int pontosBase){
        //pontos base da questão * (tempo restante / tempo total) -> arredondado
        double proporcaoTempo = (double) tempoRestante / TEMPO_TOTAL;
        return (int) Math.round(pontosBase * proporcaoTempo);
    }


}