package com.equipevii.triviamaster.model;

public class Question {
    private int id;
    private String category;
    private String difficulty;
    private String statement;
    private String correctAnswer;
    private String[] wrongAnswers;

    public Question(int id, String category, String difficulty, String statement,
                    String correctAnswer, String[] wrongAnswers){
        this.id = id;
        this.category = category;
        this.difficulty = difficulty;
        this.statement = statement;
        this.correctAnswer = correctAnswer;
        this.wrongAnswers = wrongAnswers;
    }

    public int getId(){ return id; }
    public String getCategory(){ return category; }
    public String getDifficulty(){ return difficulty; }
    public String getStatement(){ return statement; }
    public String getCorrectAnswer(){ return correctAnswer; }
    public String[] getWrongAnswers(){ return wrongAnswers; }
}
