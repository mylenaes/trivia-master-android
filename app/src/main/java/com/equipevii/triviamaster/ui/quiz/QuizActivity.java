package com.equipevii.triviamaster.ui.quiz;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;    //objeto pra manipular interface
    private LinearLayout opcaoSelecionada;
    private String opcaoCorreta;

    private QuizViewModel quizViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater()); //ligação entre java e xml
        setContentView(binding.getRoot());  //definindo layout
        //procura instancia de view model e se não existir cria
        // isso permite que view model sobreviva à recriação da activity
        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        observarDados();
        configurarCliqueOpcoes();
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
            //checar se avancou a questao ou se chegou no final
            boolean avancou = quizViewModel.avancarQuestao();

            if(avancou){
                resetarUIQuestao();
            } else{
                Toast.makeText(
                        this,
                        "Fim do quiz!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void checarResposta(LinearLayout opcao, String resposta){
        opcaoSelecionada = opcao;

        //solicitar que view model verifique a resposta
        quizViewModel.verificarResposta(resposta);
    }

    private TextView getTextoAlternativa(LinearLayout opcao){
        return (TextView) opcao.getChildAt(1); //segundo elemento do linearlayout da opção
    }

    private void destacarRespostaCorreta(){
        if(binding.tvOpcaoA.getText().toString().equals(opcaoCorreta)){
            binding.opcaoA.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );
            binding.tvOpcaoA.setTextColor(getColor(R.color.verde));
        } else if(binding.tvOpcaoB.getText().toString().equals(opcaoCorreta)){
            binding.opcaoB.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );
            binding.tvOpcaoB.setTextColor(getColor(R.color.verde));
        } else if(binding.tvOpcaoC.getText().toString().equals(opcaoCorreta)){
            binding.opcaoC.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );

            binding.tvOpcaoC.setTextColor(getColor(R.color.verde));
        } else if(binding.tvOpcaoD.getText().toString().equals(opcaoCorreta)){
            binding.opcaoD.setBackgroundResource(
                    R.drawable.bg_opcao_correta
            );

            binding.tvOpcaoD.setTextColor(getColor(R.color.verde));
        }
    }

    private void resetarUIQuestao(){
        //reseta variáveis
        opcaoSelecionada = null;

        //reseta alternativas
        binding.opcaoA.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoA.setTextColor(getColor(R.color.azulPrincipal));
        binding.opcaoB.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoB.setTextColor(getColor(R.color.azulPrincipal));
        binding.opcaoC.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoC.setTextColor(getColor(R.color.azulPrincipal));
        binding.opcaoD.setBackgroundResource(
                R.drawable.bg_opcao
        );
        binding.tvOpcaoD.setTextColor(getColor(R.color.azulPrincipal));

        //esconde feedback
        binding.tvFeedback.setVisibility(View.GONE);

        //desativa botão de próximo
        binding.btnProximo.setEnabled(false);
    }

    private void observarDados(){
        //observar questao atual e sua opcao correta
        quizViewModel.getQuestaoAtual().observe(this, questao -> {
            binding.tvQuestao.setText(questao.getEnunciado());
            opcaoCorreta = questao.getRespostaCorreta();
        });

        //observar alternativas
        quizViewModel.getAlternativasAtuais().observe(this, alternativas -> {
             binding.tvOpcaoA.setText(alternativas.get(0));
             binding.tvOpcaoB.setText(alternativas.get(1));
             binding.tvOpcaoC.setText(alternativas.get(2));
             binding.tvOpcaoD.setText(alternativas.get(3));
        });

        //observar progresso
        quizViewModel.getProgresso().observe(this, progresso -> {
            binding.progressoQuiz.setProgress(progresso);
        });

        //observar numero da questao
        quizViewModel.getNumeroQuestao().observe(this, numeroQuestao ->{
            binding.tvNumeroQuestao.setText("Questão: " + numeroQuestao);
        });

        //observar se acertou ou errou
        quizViewModel.getRespostaCorreta().observe(this, correta ->{
            //se ainda não teve resposta
            if(correta == null){
                return;
            }

            if(correta){
                //acertou
                opcaoSelecionada.setBackgroundResource(R.drawable.bg_opcao_correta);
                getTextoAlternativa(opcaoSelecionada).setTextColor(getColor(R.color.verde));
                binding.tvFeedback.setText(getString(R.string.correta));
                binding.tvFeedback.setTextColor(getColor(R.color.verde));
            } else{
                //errou
                opcaoSelecionada.setBackgroundResource(R.drawable.bg_opcao_incorreta);
                getTextoAlternativa(opcaoSelecionada).setTextColor(getColor(R.color.vermelho));
                destacarRespostaCorreta();
                binding.tvFeedback.setText(getString(R.string.incorreta));
                binding.tvFeedback.setTextColor(getColor(R.color.vermelho));
            }
            //mostrar feedback
            binding.tvFeedback.setVisibility(View.VISIBLE);
            //habilitar botão próximo
            binding.btnProximo.setEnabled(true);
        });

        quizViewModel.getPontuacao().observe(this, pontos ->{
            binding.tvPontuacao.setText("Pontos: " + pontos);
        });

        //observar tempo restante
        quizViewModel.getTempoRestante().observe(this, tempo -> {
            int segundos = (int) (tempo / 1000); //segundos restantes
            binding.tvTimer.setText(segundos + "s");

            //ficar vermelho se tiver 10s ou menos
            if(segundos > 10){
                binding.tvTimer.setBackgroundResource(R.drawable.bg_circulo);
                binding.tvTimer.setTextColor(getColor(R.color.azulPrincipal));
            } else{
                binding.tvTimer.setBackgroundResource(R.drawable.bg_circulo_vermelho);
                binding.tvTimer.setTextColor(getColor(R.color.branco));
            }
        });

        quizViewModel.getTempoEsgotado().observe(this, esgotado ->{
            //se o tempo não tiver esgotado não faz nada
            if(esgotado == null || !esgotado){
                return;
            }

            //se estiver, mostra resposta correta, feedback e habilita botão próximo
            destacarRespostaCorreta();
            binding.tvFeedback.setText(getString(R.string.esgotado));
            binding.tvFeedback.setTextColor(getColor(R.color.vermelho));
            binding.tvFeedback.setVisibility(View.VISIBLE);
            binding.btnProximo.setEnabled(true);
        });
    }


}