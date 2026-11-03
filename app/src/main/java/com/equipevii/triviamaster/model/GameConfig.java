package com.equipevii.triviamaster.model;

public class GameConfig {
    private String playerName;
    private String category;
    private String difficulty;
    private int quantity;

    public GameConfig(String playerName, String category, String difficulty, int quantity){
        this.playerName = playerName;
        this.category = category;
        this.difficulty = difficulty;
        this.quantity = quantity;
    }

    public String getPlayerName(){ return playerName; }
    public String getCategory(){ return category; }
    public String getDifficulty(){ return difficulty; }
    public int getQuantity(){ return quantity; }
}
