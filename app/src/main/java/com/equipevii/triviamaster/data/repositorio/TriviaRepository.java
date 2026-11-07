package com.equipevii.triviamaster.data.repositorio;
import com.equipevii.triviamaster.model.*;
import java.util.List;

public interface TriviaRepository {
    void getQuestions(ConfigPartida config, Callback<List<Questao>> callback);
    void saveMatch(Partida partida, Callback<Long> callback);
    void getHistory(Callback<List<Partida>> callback);

    interface Callback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}
