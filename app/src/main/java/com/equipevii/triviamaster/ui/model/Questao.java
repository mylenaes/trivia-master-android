package com.equipevii.triviamaster.ui.model;

import java.util.List;
public class Questao {
    private int id;
    private String category;
    private String dificulty;
    private String statement;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public Questao(int id, String category, String dificulty, String statement, String correctAnswer, List<String> incorrectAnswers){
        this.id = id;
        this.category = category;
        this.dificulty = dificulty;
        this.statement = statement;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public int getId(){
        return id;
    }

    public String getCategory(){
        return category;
    }

    public String getDificulty(){
        return dificulty;
    }

    public String getStatement(){
        return statement;
    }

    public String getCorrectAnswer(){
        return correctAnswer;
    }

    public List<String> getIncorrectAnswers(){
        return incorrectAnswers;
    }
}
