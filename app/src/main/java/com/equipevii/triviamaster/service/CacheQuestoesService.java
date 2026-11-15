package com.equipevii.triviamaster.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.equipevii.triviamaster.data.repositorio.RepositorioTriviaImpl;
import com.equipevii.triviamaster.data.repositorio.TriviaRepository;
import com.equipevii.triviamaster.model.ConfigPartida;

/*
 * Pré-carrega questões da API no cache para o modo offline.
 * É iniciado com startService() enquanto o app está aberto
 * (pelo ConnectivityReceiver quando a internet volta) e termina
 * sozinho com stopSelf(), por isso não precisa de foreground service.
 */
public class CacheQuestoesService extends Service {

    // 50 é o máximo que a API aceita por requisição
    private static final int QUANTIDADE_PRE_CARREGADA = 50;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        RepositorioTriviaImpl repositorio = new RepositorioTriviaImpl(this);

        // Categoria e dificuldade vazias = questões variadas
        ConfigPartida config = new ConfigPartida("", "", "", QUANTIDADE_PRE_CARREGADA);

        repositorio.atualizarCache(config, new TriviaRepository.Callback<Integer>() {
            @Override
            public void onSuccess(Integer quantidade) {
                stopSelf(startId);
            }

            @Override
            public void onError(String message) {
                // Sem internet ou API fora: tenta de novo na próxima vez que for iniciado
                stopSelf(startId);
            }
        });

        // Se o sistema matar o Service, não precisa recriar
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service iniciado, não vinculado
        return null;
    }
}
