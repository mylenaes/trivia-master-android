package com.equipevii.triviamaster.ui.history;
import com.equipevii.triviamaster.ui.BaseActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import com.equipevii.triviamaster.R;

public class HistoricoActivity extends BaseActivity {
    private HistoricoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageButton btn = findViewById(R.id.historyButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        RecyclerView recycler = findViewById(R.id.historyView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoricoAdapter(null);
        recycler.setAdapter(adapter);

        HistoricoExecutor executor = new ViewModelProvider(this).
                get(HistoricoExecutor.class);

        executor.getCursor().observe(this, cursor -> {
            adapter.swapCursor(cursor);
        });
    }
}

/*
 * Alteração (Mylena):
 * - Agora estende BaseActivity em vez de AppCompatActivity, para tratar
 *   o espaço da barra de status do Android 15 igual às outras telas.
 */