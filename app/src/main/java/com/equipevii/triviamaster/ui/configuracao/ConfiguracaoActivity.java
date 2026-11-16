package com.equipevii.triviamaster.ui.configuracao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.databinding.ActivityConfiguracaoBinding;
import com.equipevii.triviamaster.ui.BaseActivity;
import com.equipevii.triviamaster.ui.inicio.MainActivity;
import com.equipevii.triviamaster.ui.quiz.QuizActivity;

public class ConfiguracaoActivity extends BaseActivity {
    private final String[] nomesCategorias = {
            "Qualquer categoria",
            "Conhecimentos gerais",
            "Livros",
            "Filmes",
            "Música",
            "Videogames",
            "Ciência e natureza",
            "Computação",
            "Matemática",
            "Esportes",
            "Geografia",
            "História",
            "Animais"
    };
    private final String[] idsCategorias = {
            "", "9", "10", "11", "12", "15", "17", "18", "19", "21", "22", "23", "27"
    };

    private ActivityConfiguracaoBinding binding;
    private String nomeJogador;
    private String dificuldadeEscolhida = "medium";
    private int quantidadeEscolhida = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nomeJogador = getIntent().getStringExtra(MainActivity.EXTRA_NOME);
        if (nomeJogador == null) {
            nomeJogador = "Jogador";
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nomesCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCategoria.setAdapter(adapter);

        configurarDificuldade();
        configurarQuantidade();

        marcarDificuldade(binding.btnMedio);
        marcarQuantidade(binding.btnQtd10);

        binding.btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarPartida();
            }
        });

        View.OnClickListener voltar = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        binding.btnVoltar.setOnClickListener(voltar);
        binding.btnVoltarTopo.setOnClickListener(voltar);
    }

    private void configurarDificuldade() {
        binding.btnFacil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dificuldadeEscolhida = "easy";
                marcarDificuldade(binding.btnFacil);
            }
        });
        binding.btnMedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dificuldadeEscolhida = "medium";
                marcarDificuldade(binding.btnMedio);
            }
        });
        binding.btnDificil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dificuldadeEscolhida = "hard";
                marcarDificuldade(binding.btnDificil);
            }
        });
    }

    private void configurarQuantidade() {
        binding.btnQtd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantidadeEscolhida = 5;
                marcarQuantidade(binding.btnQtd5);
            }
        });
        binding.btnQtd10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantidadeEscolhida = 10;
                marcarQuantidade(binding.btnQtd10);
            }
        });
        binding.btnQtd15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantidadeEscolhida = 15;
                marcarQuantidade(binding.btnQtd15);
            }
        });
    }
    private void marcarDificuldade(TextView escolhido) {
        desmarcar(binding.btnFacil);
        desmarcar(binding.btnMedio);
        desmarcar(binding.btnDificil);
        marcar(escolhido);
    }

    private void marcarQuantidade(TextView escolhido) {
        desmarcar(binding.btnQtd5);
        desmarcar(binding.btnQtd10);
        desmarcar(binding.btnQtd15);
        marcar(escolhido);
    }

    private void marcar(TextView botao) {
        botao.setBackgroundResource(R.drawable.bg_chip_selecionado);
        botao.setTextColor(getColor(R.color.branco));
    }

    private void desmarcar(TextView botao) {
        botao.setBackgroundResource(R.drawable.bg_chip);
        botao.setTextColor(getColor(R.color.azulPrincipal));
    }

    private void iniciarPartida() {
        int posicaoCategoria = binding.spCategoria.getSelectedItemPosition();
        String categoria = idsCategorias[posicaoCategoria];
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra(QuizActivity.EXTRA_NOME, nomeJogador);
        intent.putExtra(QuizActivity.EXTRA_CATEGORIA, categoria);
        intent.putExtra(QuizActivity.EXTRA_DIFICULDADE, dificuldadeEscolhida);
        intent.putExtra(QuizActivity.EXTRA_QUANTIDADE, quantidadeEscolhida);
        startActivity(intent);
    }
}