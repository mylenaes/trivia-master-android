package com.equipevii.triviamaster.ui.history;
import com.equipevii.triviamaster.data.local.ContratoTrivia;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoricoExecutor extends AndroidViewModel {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Cursor> cursor = new MutableLiveData<>();

    public HistoricoExecutor(@NonNull Application app){
        super(app);
        carregarHistorico();
    }

    public LiveData<Cursor> getCursor(){
        return cursor;
    }

    private void carregarHistorico(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Uri uri = ContratoTrivia.RegistroPartida.CONTENT_URI;
                Cursor newCursor = getApplication().getContentResolver().query(uri,null,
                        null,null, ContratoTrivia.RegistroPartida._ID + " DESC");

                cursor.postValue(newCursor);
            }
        });
    }

    @Override
    protected void onCleared(){
        super.onCleared();
        executor.shutdown();
    }
}
