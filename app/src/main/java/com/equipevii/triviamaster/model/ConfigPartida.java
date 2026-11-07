package com.equipevii.triviamaster.model;

public class ConfigPartida {
    private String nomeJogador;
    private String categoria;
    private String dificuldade;
    private int quantidade;

    public ConfigPartida(String nomeJogador, String categoria, String dificuldade, int quantidade){
        this.nomeJogador = nomeJogador;
        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.quantidade = quantidade;
    }

    public String getNomeJogador(){ return nomeJogador; }
    public String getCategoria(){ return categoria; }
    public String getDificuldade(){ return dificuldade; }
    public int getQuantidade(){ return quantidade; }
}
