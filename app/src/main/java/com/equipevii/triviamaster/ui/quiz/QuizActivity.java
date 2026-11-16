package com.equipevii.triviamaster.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.databinding.ActivityQuizBinding;
import com.equipevii.triviamaster.model.ConfigPartida;
import com.equipevii.triviamaster.model.Partida;
import com.equipevii.triviamaster.ui.BaseActivity;
import com.equipevii.triviamaster.ui.resultado.ResultadoActivity;

public class QuizActivity extends BaseActivity {
    //chaves usadas pela tela de configuração para mandar os dados
    public static final String EXTRA_NOME = "nome";
    public static final String EXTRA_CATEGORIA = "categoria";
    public static final String EXTRA_DIFICULDADE = "dificuldade";
    public static final String EXTRA_QUANTIDADE = "quantidade";

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

        //montar a configuração com o que veio da tela anterior
        //(sem extras = 10 questões de qualquer categoria e dificuldade)
        ConfigPartida config = new ConfigPartida(
                getIntent().getStringExtra(EXTRA_NOME),
                getIntent().getStringExtra(EXTRA_CATEGORIA),
                getIntent().getStringExtra(EXTRA_DIFICULDADE),
                getIntent().getIntExtra(EXTRA_QUANTIDADE, 10)
        );
        quizViewModel.carregarQuestoes(config);
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
                //acabou: salva a partida no histórico
                binding.btnProximo.setEnabled(false);
                quizViewModel.finalizarPartida();
            }
        });
    }

    private void abrirResultado(){
        Partida partida = quizViewModel.getPartidaFinal();

        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra(ResultadoActivity.EXTRA_NOME, partida.getJogador());
        intent.putExtra(ResultadoActivity.EXTRA_ACERTOS, quizViewModel.getAcertos());
        intent.putExtra(ResultadoActivity.EXTRA_TOTAL, quizViewModel.getTotalQuestoes());
        intent.putExtra(ResultadoActivity.EXTRA_PONTUACAO, quizViewModel.getPontuacaoFinal());
        intent.putExtra(ResultadoActivity.EXTRA_CATEGORIA, partida.getCategoria());
        intent.putExtra(ResultadoActivity.EXTRA_DIFICULDADE, partida.getDificuldade());
        startActivity(intent);

        //fecha o quiz para o botão voltar não cair numa partida que já acabou
        finish();
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

            //se girar a tela a activity é recriada e perde a opção clicada,
            //então só pinta a opção se ela ainda existir
            if(correta){
                //acertou
                if(opcaoSelecionada != null){
                    opcaoSelecionada.setBackgroundResource(R.drawable.bg_opcao_correta);
                    getTextoAlternativa(opcaoSelecionada).setTextColor(getColor(R.color.verde));
                } else{
                    destacarRespostaCorreta();
                }
                binding.tvFeedback.setText(getString(R.string.correta));
                binding.tvFeedback.setTextColor(getColor(R.color.verde));
            } else{
                //errou
                if(opcaoSelecionada != null){
                    opcaoSelecionada.setBackgroundResource(R.drawable.bg_opcao_incorreta);
                    getTextoAlternativa(opcaoSelecionada).setTextColor(getColor(R.color.vermelho));
                }
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

        //se não conseguiu buscar as questões, avisa e volta pra tela anterior
        quizViewModel.getErro().observe(this, mensagem ->{
            if(mensagem == null){
                return;
            }
            Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
            finish();
        });

        //quando terminar de salvar a partida vai para o resultado
        quizViewModel.getPartidaSalva().observe(this, salvou ->{
            if(salvou == null){
                return;
            }
            if(!salvou){
                Toast.makeText(this, "Não foi possível salvar a partida no histórico.",
                        Toast.LENGTH_SHORT).show();
            }
            abrirResultado();
        });
    }
}

/*
 * Alterações (Mylena):
 * - Estende BaseActivity (barra de status do Android 15).
 * - Recebe nome, categoria, dificuldade e quantidade pela Intent (EXTRA_...)
 *   e pede as questões ao ViewModel com carregarQuestoes(config).
 * - Na última questão, finalizarPartida() salva no histórico e abrirResultado()
 *   abre a tela de resultado (antes era só um Toast).
 * - Corrigido crash ao girar a tela: opcaoSelecionada fica null quando a
 *   activity é recriada, então agora só pinta a opção se ela existir.
 * - Novos observers: getErro (sem questões, avisa e volta) e
 *   getPartidaSalva (abre o resultado depois de salvar).
 */