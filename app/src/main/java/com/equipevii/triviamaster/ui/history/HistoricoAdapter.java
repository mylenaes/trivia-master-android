package com.equipevii.triviamaster.ui.history;

import android.database.Cursor;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.equipevii.triviamaster.R;

public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder>{
    private Cursor cursor;
    public HistoricoAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView cardPlayer;
        public final TextView cardCategoryDifficulty;
        public final TextView cardDateHour;
        public final TextView cardScore;

        public ViewHolder(View view){
            super(view);
            cardPlayer = view.findViewById(R.id.cardPlayer);
            cardCategoryDifficulty = view.findViewById(R.id.cardCategoryDifficulty);
            cardDateHour = view.findViewById(R.id.cardDateHour);
            cardScore = view.findViewById(R.id.cardScore);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        if(cursor != null && cursor.moveToPosition(position)){
            int playerIndex = cursor.getColumnIndex("partida_jogador");
            int categoryIndex = cursor.getColumnIndex("partida_categoria");
            int difficultyIndex = cursor.getColumnIndex("partida_dificuldade");
            int scoreIndex = cursor.getColumnIndex("partida_pontuacao");
            int dateIndex = cursor.getColumnIndex("partida_data");
            int hourIndex = cursor.getColumnIndex("partida_horario");

            if(playerIndex != -1) holder.cardPlayer.setText(cursor.getString(playerIndex));

            if(categoryIndex !=  -1 && difficultyIndex != - 1){
                String category = cursor.getString(categoryIndex);
                String difficulty = cursor.getString(difficultyIndex);
                String text = holder.itemView.getContext().getString(
                        R.string.category_difficulty,
                        category,
                        difficulty);
                holder.cardCategoryDifficulty.setText(text);
            }

            if(dateIndex != -1 && hourIndex != -1){
                String date = cursor.getString(dateIndex);
                String hour = cursor.getString(hourIndex);
                String text = holder.itemView.getContext().getString(
                        R.string.date_hour,
                        date,
                        hour);
                holder.cardDateHour.setText(text);
            }

            if(scoreIndex != -1 && holder.cardScore != null){
                holder.cardScore.setText(cursor.getString(scoreIndex));
            }
        }
    }

    @Override
    public int getItemCount(){
        if(cursor != null){
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor){
        if(cursor != null){
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}
