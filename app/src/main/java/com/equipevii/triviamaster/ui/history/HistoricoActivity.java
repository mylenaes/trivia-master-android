package com.equipevii.triviamaster.ui.history;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageButton;
import com.equipevii.triviamaster.R;
import com.equipevii.triviamaster.data.local.ContratoTrivia;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private HistoricoAdapter adapter;
    private ImageButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btn = findViewById(R.id.historyButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        recycler = findViewById(R.id.historyView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoricoAdapter(null);
        recycler.setAdapter(adapter);

        carregarHistorico();
    }

    private void carregarHistorico(){
        Uri uri = ContratoTrivia.BASE_CONTENT_URI;
        Cursor cursor = getContentResolver().query(
                uri,
                null,
                null,
                null,
                "partida_data DESC"
        );

        adapter.swapCursor(cursor);
    }
}