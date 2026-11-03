package com.equipevii.triviamaster.ui.quiz;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;    //objeto pra manipular interface
    private LinearLayout selectedOption;    //opção selecionada
    private String correctAnswer = "Kotlin";    //opção coreta
    private boolean answered = false;   //se já respondeu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater()); //ligação entre java e xml
        setContentView(binding.getRoot());  //definindo layout
        setupOptions(); //configurações de eventos de clique
    }

    private void setupOptions(){
        binding.optionA.setOnClickListener(v ->
                checkAnswer(binding.optionA, "Swift"));

        binding.optionB.setOnClickListener(v ->
                checkAnswer(binding.optionB, "Kotlin"));

        binding.optionC.setOnClickListener(v ->
                checkAnswer(binding.optionC, "Ruby"));

        binding.optionD.setOnClickListener(v ->
                checkAnswer(binding.optionD, "PHP"));

        binding.btnNext.setOnClickListener(v ->{

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
        if(correctAnswer.equals("Swift")){
            binding.optionA.setBackgroundResource(
                    R.drawable.bg_option_correct
            );
            binding.tvOptionA.setTextColor(Color.parseColor("#1EAB53"));
        } else if(correctAnswer.equals("Kotlin")){
            binding.optionB.setBackgroundResource(
                    R.drawable.bg_option_correct
            );
            binding.tvOptionB.setTextColor(Color.parseColor("#1EAB53"));
        } else if(correctAnswer.equals("Ruby")){
            binding.optionC.setBackgroundResource(
                    R.drawable.bg_option_correct
            );

            binding.tvOptionC.setTextColor(Color.parseColor("#1EAB53"));
        } else if(correctAnswer.equals("PHP")){
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

}