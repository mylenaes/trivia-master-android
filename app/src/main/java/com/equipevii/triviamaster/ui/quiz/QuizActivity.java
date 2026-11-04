package com.equipevii.triviamaster.ui.quiz;

import android.os.Bundle;
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

    private void configurarCliqueOpcoes(){
        binding.optionA.setOnClickListener(v ->
                checarResposta(
                        binding.optionA,
                        binding.tvOptionA.getText().toString()));

        binding.optionB.setOnClickListener(v ->
                checarResposta(
                        binding.optionB,
                        binding.tvOptionB.getText().toString()));

        binding.optionC.setOnClickListener(v ->
                checarResposta(
                        binding.optionC,
                        binding.tvOptionC.getText().toString()));

        binding.optionD.setOnClickListener(v ->
                checarResposta(
                        binding.optionD,
                        binding.tvOptionD.getText().toString()));

        binding.btnNext.setOnClickListener(v ->{
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

        boolean correta = answer.equals(opcaoCorreta);   //verificar se tá correto

        if(correta){
            //destaca que a alternativa é correta
            opcaoSelecionada.setBackgroundResource(
              R.drawable.bg_option_correct
            );
            getTextoAlternativa(option).setTextColor(Color.parseColor("#1EAB53"));

            //calcular pontuação da questão de acordo com a dificuldade
            Questao questaoAtual = listaQuestoes.get(indiceQuestaotual);
            pontuacaoAtual += calcularPontos(questaoAtual);
            atualizarPontuacao();

            //exibe feedback
            binding.tvFeedback.setText("Resposta correta!");
            binding.tvFeedback.setTextColor(Color.parseColor("#1EAB53"));
        } else{
            //destaca que a alternativa é incorreta
            opcaoSelecionada.setBackgroundResource(
                    R.drawable.bg_option_error
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
        binding.btnNext.setEnabled(true);
    }

    private TextView getTextoAlternativa(LinearLayout option){
        return (TextView) option.getChildAt(1); //segundo elemento do linearlayout da opção
    }

    private void destacarRespostaCorreta(){
        if(binding.tvOptionA.getText().toString().equals(opcaoCorreta)){
            binding.optionA.setBackgroundResource(
                    R.drawable.bg_option_correct
            );
            binding.tvOptionA.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOptionB.getText().toString().equals(opcaoCorreta)){
            binding.optionB.setBackgroundResource(
                    R.drawable.bg_option_correct
            );
            binding.tvOptionB.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOptionC.getText().toString().equals(opcaoCorreta)){
            binding.optionC.setBackgroundResource(
                    R.drawable.bg_option_correct
            );

            binding.tvOptionC.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOptionD.getText().toString().equals(opcaoCorreta)){
            binding.optionD.setBackgroundResource(
                    R.drawable.bg_option_correct
            );

            binding.tvOptionD.setTextColor(Color.parseColor("#1EAB53"));
        }
    }

    private void resetarUIQuestao(){
        //reseta variáveis
        respondeu = false;
        opcaoSelecionada = null;

        //reseta alternativas
        binding.optionA.setBackgroundResource(
                R.drawable.bg_option
        );
        binding.tvOptionA.setTextColor(Color.parseColor("#192858"));
        binding.optionB.setBackgroundResource(
                R.drawable.bg_option
        );
        binding.tvOptionB.setTextColor(Color.parseColor("#192858"));
        binding.optionC.setBackgroundResource(
                R.drawable.bg_option
        );
        binding.tvOptionC.setTextColor(Color.parseColor("#192858"));
        binding.optionD.setBackgroundResource(
                R.drawable.bg_option
        );
        binding.tvOptionD.setTextColor(Color.parseColor("#192858"));

        //esconde feedback
        binding.tvFeedback.setVisibility(View.GONE);

        //desativa botão de próximo
        binding.btnNext.setEnabled(false);
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
        binding.progressQuiz.setProgress(progresso);

        //atualiza enunciado
        binding.tvQuestion.setText(questaoAtual.getEnunciado());

        opcaoCorreta = questaoAtual.getRespostaCorreta();

        alternativasAtuais = new ArrayList<>(
                questaoAtual.getRespostasErradas()
        );
        alternativasAtuais.add(opcaoCorreta);

        //embaralhar as opções
        Collections.shuffle(alternativasAtuais);

        //exibir as opções uma em cada textview
        binding.tvOptionA.setText(alternativasAtuais.get(0));
        binding.tvOptionB.setText(alternativasAtuais.get(1));
        binding.tvOptionC.setText(alternativasAtuais.get(2));
        binding.tvOptionD.setText(alternativasAtuais.get(3));

        //atualizar numero da questão
        binding.tvQuestionNumber.setText("Questão: " + (indiceQuestaotual + 1));

        //resetar pra nova questão
        resetarUIQuestao();
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
        binding.tvScore.setText("Pontos: " + pontuacaoAtual);
    }

}