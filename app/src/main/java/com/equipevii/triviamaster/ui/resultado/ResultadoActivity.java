package com.equipevii.triviamaster.ui.resultado;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.equipevii.triviamaster.databinding.ActivityResultadoBinding;
import com.equipevii.triviamaster.ui.BaseActivity;
import com.equipevii.triviamaster.ui.inicio.MainActivity;

public class ResultadoActivity extends BaseActivity {
    public static final String EXTRA_NOME = "nome";
    public static final String EXTRA_ACERTOS = "acertos";
    public static final String EXTRA_TOTAL = "total";
    public static final String EXTRA_PONTUACAO = "pontuacao";
    public static final String EXTRA_CATEGORIA = "categoria";
    public static final String EXTRA_DIFICULDADE = "dificuldade";

    private ActivityResultadoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String nome = getIntent().getStringExtra(EXTRA_NOME);
        int acertos = getIntent().getIntExtra(EXTRA_ACERTOS, 0);
        int total = getIntent().getIntExtra(EXTRA_TOTAL, 0);
        int pontuacao = getIntent().getIntExtra(EXTRA_PONTUACAO, 0);
        String categoria = getIntent().getStringExtra(EXTRA_CATEGORIA);
        String dificuldade = getIntent().getStringExtra(EXTRA_DIFICULDADE);

        if (nome == null || nome.isEmpty()) {
            nome = "Jogador";
        }
        int erros = total - acertos;
        int porcentagem = 0;
        if (total > 0) {
            porcentagem = (acertos * 100) / total;
        }

        String mensagem;
        if (porcentagem >= 80) {
            mensagem = "Excelente!";
        } else if (porcentagem >= 50) {
            mensagem = "Bom trabalho!";
        } else {
            mensagem = "Continue praticando!";
        }

        binding.tvMensagem.setText(mensagem);
        binding.tvSubtitulo.setText(nome + ", veja seu desempenho");
        binding.tvPlacar.setText(acertos + "/" + total);
        binding.tvPorcentagem.setText(porcentagem + "% de acertos");
        binding.tvAcertos.setText(String.valueOf(acertos));
        binding.tvErros.setText(String.valueOf(erros));
        binding.tvPontuacaoFinal.setText(pontuacao + " pontos");
        binding.tvCategoriaDificuldade.setText(categoria + " · " + dificuldade);
        binding.btnJogarNovamente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultadoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
}