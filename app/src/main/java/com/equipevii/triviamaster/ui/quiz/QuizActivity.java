package com.equipevii.triviamaster.ui.quiz;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.databinding.ActivityQuizBinding;
import com.equipevii.triviamaster.ui.model.Questao;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;    //objeto pra manipular interface
    private LinearLayout selectedOption;    //opção selecionada
    private String correctAnswer = "Kotlin";    //opção coreta
    private boolean answered = false;   //se já respondeu
    private List<Questao> questionsList;    //questões da partida
    private int actualQuestionIndex = 0;    //em qual questão está
    private List<String> actualOptions; //opções da questão atual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater()); //ligação entre java e xml
        setContentView(binding.getRoot());  //definindo layout
        createQuestionsList();  //criando lista de questões
        setupOptions(); //configurações de eventos de clique
        loadQuestion(); //carregando questão
    }

    private void setupOptions(){
        binding.optionA.setOnClickListener(v ->
                checkAnswer(
                        binding.optionA,
                        binding.tvOptionA.getText().toString()));

        binding.optionB.setOnClickListener(v ->
                checkAnswer(
                        binding.optionB,
                        binding.tvOptionB.getText().toString()));

        binding.optionC.setOnClickListener(v ->
                checkAnswer(
                        binding.optionC,
                        binding.tvOptionC.getText().toString()));

        binding.optionD.setOnClickListener(v ->
                checkAnswer(
                        binding.optionD,
                        binding.tvOptionD.getText().toString()));

        binding.btnNext.setOnClickListener(v ->{
            //se ainda tiverem questões, passa pra próxima
            if(actualQuestionIndex < questionsList.size() - 1){
                actualQuestionIndex++;
                loadQuestion();
            } else{ //se chegar oa final das questões diz que acabou
                Toast.makeText(
                        this,
                        "Fim do quiz!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void checkAnswer(LinearLayout option, String answer){
        if(answered){   //se já respondeu, não pode responder de novo
            return;
        }

        answered = true;
        selectedOption = option;

        boolean isCorrect = answer.equals(correctAnswer);   //verificar se tá correto

        if(isCorrect){
            //destaca que a alternativa é correta
            selectedOption.setBackgroundResource(
              R.drawable.bg_option_correct
            );
            getOptionText(option).setTextColor(Color.parseColor("#1EAB53"));

            //exibe feedback
            binding.tvFeedback.setText("Resposta correta!");
            binding.tvFeedback.setTextColor(Color.parseColor("#1EAB53"));
        } else{
            //destaca que a alternativa é incorreta
            selectedOption.setBackgroundResource(
                    R.drawable.bg_option_error
            );
            getOptionText(option).setTextColor(Color.parseColor("#E23439"));

            //destaca alternativa correta
            highlightCorrectAnswer();

            //exibe feedback
            binding.tvFeedback.setText("Resposta incorreta!");
            binding.tvFeedback.setTextColor(Color.parseColor("#E23439"));
        }

        //tornar feedback visível
        binding.tvFeedback.setVisibility(View.VISIBLE);

        //tornar botão de próximo clicável
        binding.btnNext.setEnabled(true);
    }

    private TextView getOptionText(LinearLayout option){
        return (TextView) option.getChildAt(1); //segundo elemento do linearlayout da opção
    }

    private void highlightCorrectAnswer(){
        if(binding.tvOptionA.getText().toString().equals(correctAnswer)){
            binding.optionA.setBackgroundResource(
                    R.drawable.bg_option_correct
            );
            binding.tvOptionA.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOptionB.getText().toString().equals(correctAnswer)){
            binding.optionB.setBackgroundResource(
                    R.drawable.bg_option_correct
            );
            binding.tvOptionB.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOptionC.getText().toString().equals(correctAnswer)){
            binding.optionC.setBackgroundResource(
                    R.drawable.bg_option_correct
            );

            binding.tvOptionC.setTextColor(Color.parseColor("#1EAB53"));
        } else if(binding.tvOptionD.getText().toString().equals(correctAnswer)){
            binding.optionD.setBackgroundResource(
                    R.drawable.bg_option_correct
            );

            binding.tvOptionD.setTextColor(Color.parseColor("#1EAB53"));
        }
    }

    //resetar pra próxima questão
    private void resetQuestionUI(){
        //reseta variáveis
        answered = false;
        selectedOption = null;

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

    private void createQuestionsList(){
        questionsList = new ArrayList<>();

        questionsList.add(new Questao(
                1,
                "Computadores",
                "Médio",
                "Qual linguagem é usada no Android Studio além de Java?",
                "Kotlin",
                Arrays.asList("Swift", "Ruby", "PHP")
        ));
        questionsList.add(new Questao(
                2,
                "Computadores",
                "Médio",
                "Quantos bits tem um byte?",
                "8",
                Arrays.asList("4", "16", "32")
        ));
        questionsList.add(new Questao(
                3,
                "Computadores",
                "Médio",
                "Qual porta padrão do HTTPS?",
                "443",
                Arrays.asList("80", "21", "8080")
        ));
        questionsList.add(new Questao(
                4,
                "Computadores",
                "Médio",
                "Qual empresa criou o Android?",
                "Google",
                Arrays.asList("Apple", "Microsoft", "Samsung")
        ));
        questionsList.add(new Questao(
                5,
                "Computadores",
                "Médio",
                "O que significa CPU?",
                "Central Processing Unit",
                Arrays.asList("Computer Power Unit", "Core Program Utility", "Central Print Unit")
        ));
    }

    private void loadQuestion(){
        //pega a questão atual
        Questao actualQuestion = questionsList.get(actualQuestionIndex);

        //calcular e atualizar progressbar
        int progress = ((actualQuestionIndex + 1) * 100) / questionsList.size();
        binding.progressQuiz.setProgress(progress);

        //atualiza enunciado
        binding.tvQuestion.setText(actualQuestion.getStatement());

        //respota correta
        correctAnswer = actualQuestion.getCorrectAnswer();

        //opções atuais
        actualOptions = new ArrayList<>(
                actualQuestion.getIncorrectAnswers()
        );
        actualOptions.add(correctAnswer);

        //embaralhar as opções
        Collections.shuffle(actualOptions);

        //exibir as opções uma em cada textview
        binding.tvOptionA.setText(actualOptions.get(0));
        binding.tvOptionB.setText(actualOptions.get(1));
        binding.tvOptionC.setText(actualOptions.get(2));
        binding.tvOptionD.setText(actualOptions.get(3));

        //atualizar numero da questão
        binding.tvQuestionNumber.setText("Questão: " + (actualQuestionIndex + 1));

        //resetar pra nova questão
        resetQuestionUI();
    }

}