package com.equipevii.triviamaster.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.View;
import com.equipevii.triviamaster.service.CacheQuestoesService;

public class ConnectivityReceiver extends BroadcastReceiver {
    private static boolean cacheAtualizado = false;
    private boolean estavaSemInternet = false;
    private final View avisoSemConexao;

    public ConnectivityReceiver(View avisoSemConexao) {
        this.avisoSemConexao = avisoSemConexao;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager gerenciador =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE
                );
        boolean temInternet = false;
        if (gerenciador != null) {
            Network rede = gerenciador.getActiveNetwork();
            if (rede != null) {
                NetworkCapabilities capacidades = gerenciador.getNetworkCapabilities(rede);
                if (capacidades != null) {
                    temInternet = capacidades.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_INTERNET
                    );
                }
            }
        }
        if (!temInternet) {
            avisoSemConexao.setVisibility(View.VISIBLE);
            estavaSemInternet = true;
            return;
        }
        avisoSemConexao.setVisibility(View.GONE);
        if (!cacheAtualizado || estavaSemInternet) {
            Intent servico = new Intent(context, CacheQuestoesService.class);
            context.startService(servico);
            cacheAtualizado = true;
        }
        estavaSemInternet = false;
    }
}