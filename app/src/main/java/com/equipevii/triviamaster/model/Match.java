package com.equipevii.triviamaster.model;

import kotlin.TuplesKt;

public class Match {
    private int id;
    private String player;
    private String category;
    private String difficulty;
    private int correctGuesses;
    private int totalGuesses;
    private int score;
    private String time;

    public Match(int id, String player, String category, String difficulty, int correctGuesses,
                 int totalGuesses, int score, String time){
        this.id = id;
        this.player = player;
        this.category = category;
        this.difficulty = difficulty;
        this.correctGuesses = correctGuesses;
        this.totalGuesses = totalGuesses;
        this.score = score;
        this.time = time;
    }

    public int getId(){ return id; }
    public String getPlayer(){ return player; }
    public String getCategory(){ return category; }
    public String getDifficulty(){ return difficulty; }
    public int getCorrectGuesses(){ return correctGuesses; }
    public int getTotalGuesses(){ return totalGuesses; }
    public int getScore(){ return score; }
    public String getTime(){ return time; }
}
