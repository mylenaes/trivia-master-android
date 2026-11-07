package com.equipevii.triviamaster.model;

public class Partida {
    private int id;
    private String jogador;
    private String categoria;
    private String dificuldade;
    private int acertos;
    private int total;
    private int pontuacao;
    private String data;

    public Partida(int id, String jogador, String categoria, String dificuldade, int acertos,
                   int total, int pontuacao, String data){
        this.id = id;
        this.jogador = jogador;
        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.acertos = acertos;
        this.total = total;
        this.pontuacao = pontuacao;
        this.data = data;
    }

    public int getId(){ return id; }
    public String getJogador(){ return jogador; }
    public String getCategoria(){ return categoria; }
    public String getDificuldade(){ return dificuldade; }
    public int getAcertos(){ return acertos; }
    public int getTotal(){ return total; }
    public int getPontuacao(){ return pontuacao; }
    public String getData(){ return data; }
}
