package com.equipevii.triviamaster.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.equipevii.triviamaster.R;

// TodaS as telas do app herdam desta classe.
// no Android 15 o app desenha atrás da barra de status e da barra de navegação,
// então aqui a gente empurra o conteúdo para não ficar escondido atrás delas.
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        aplicarEspacoDasBarras();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        aplicarEspacoDasBarras();
    }

    private void aplicarEspacoDasBarras() {
        ViewGroup content = findViewById(android.R.id.content);
        final View rootView = content.getChildAt(0);
        if (rootView == null) return;
        final View header = findViewById(R.id.cabecalho);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(header == null);
        final int l = rootView.getPaddingLeft();
        final int t = rootView.getPaddingTop();
        final int r = rootView.getPaddingRight();
        final int b = rootView.getPaddingBottom();

        final int headerInitialTop = (header != null) ? header.getPaddingTop() : 0;

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets sysInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            if (header != null) {
                header.setPadding(header.getPaddingLeft(), headerInitialTop + sysInsets.top, header.getPaddingRight(), header.getPaddingBottom());
                v.setPadding(l + sysInsets.left, t, r + sysInsets.right, b + sysInsets.bottom);
            } else {
                v.setPadding(l + sysInsets.left, t + sysInsets.top, r + sysInsets.right, b + sysInsets.bottom);
            }
            return insets;
        });
    }
    protected void mostrarErro(String mensagem) {
        if (mensagem == null || mensagem.isEmpty()) {
            mensagem = "Aconteceu um erro inesperado. Tente novamente!";
        }
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    protected void mostrarMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}