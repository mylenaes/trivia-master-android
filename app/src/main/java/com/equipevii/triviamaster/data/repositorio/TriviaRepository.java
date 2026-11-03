package com.equipevii.triviamaster.data.repositorio;
import com.equipevii.triviamaster.model.*;
import java.util.List;

public interface TriviaRepository {
    void getQuestions(GameConfig config, Callback<List<Question>> callback);
    void saveMatch(Match match, Callback<Long> callback);
    void getHistory(Callback<List<Match>> callback);

    interface Callback<T> {
        void onSuccess(T data);
        void onError(String message);
    }
}
