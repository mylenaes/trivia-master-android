package com.equipevii.triviamaster.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.equipevii.triviamaster.data.repositorio.RepositorioTriviaImpl;
import com.equipevii.triviamaster.data.repositorio.TriviaRepository;
import com.equipevii.triviamaster.model.ConfigPartida;

public class CacheQuestoesService extends Service {
    private static final int QUANTIDADE_PRE_CARREGADA = 50;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RepositorioTriviaImpl repositorio = new RepositorioTriviaImpl(this);
        ConfigPartida config = new ConfigPartida("", "", "", QUANTIDADE_PRE_CARREGADA);
        repositorio.atualizarCache(config, new TriviaRepository.Callback<Integer>() {
            @Override
            public void onSuccess(Integer quantidade) {
                stopSelf(startId);
            }
            @Override
            public void onError(String message) {
                stopSelf(startId);
            }
        });
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
