package com.equipevii.triviamaster.ui.inicio;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import com.equipevii.triviamaster.databinding.ActivityMainBinding;
import com.equipevii.triviamaster.receiver.ConnectivityReceiver;
import com.equipevii.triviamaster.ui.BaseActivity;
import com.equipevii.triviamaster.ui.configuracao.ConfiguracaoActivity;
import com.equipevii.triviamaster.ui.history.HistoricoActivity;

public class MainActivity extends BaseActivity {
    public static final String EXTRA_NOME = "nome";
    private ActivityMainBinding binding;
    private ConnectivityReceiver receiverConexao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        receiverConexao = new ConnectivityReceiver(binding.tvSemConexao);
        binding.btnComecar.setOnClickListener(v -> comecar());
        binding.btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoricoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filtro = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ContextCompat.registerReceiver(this, receiverConexao, filtro,
                ContextCompat.RECEIVER_NOT_EXPORTED);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiverConexao);
    }

    private void comecar() {
        String nome = binding.etNome.getText().toString().trim();
        if (nome.isEmpty()) {
            binding.etNome.setError("Digite seu nome Pra começar");
            binding.etNome.requestFocus();
            return;
        }
        if (nome.length() < 2) {
            binding.etNome.setError("O nome precisa ter pelo menos 2 letras");
            binding.etNome.requestFocus();
            return;
        }
        Intent intent = new Intent(this, ConfiguracaoActivity.class);
        intent.putExtra(EXTRA_NOME, nome);
        startActivity(intent);
    }
}