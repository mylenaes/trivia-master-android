package com.equipevii.triviamaster.model;

public class Partida {
    private int id;
    private String jogador;
    private String categoria;
    private String dificuldade;
    private int acertos;
    private int total_questoes;
    private int pontuacao;
    private String data;
    private String horario;

    public Partida(int id, String jogador, String categoria, String dificuldade, int acertos,
                   int total_questoes, int pontuacao, String data, String horario){
        this.id = id;
        this.jogador = jogador;
        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.acertos = acertos;
        this.total_questoes = total_questoes;
        this.pontuacao = pontuacao;
        this.data = data;
        this.horario = horario;
    }

    public int getId(){ return id; }
    public String getJogador(){ return jogador; }
    public String getCategoria(){ return categoria; }
    public String getDificuldade(){ return dificuldade; }
    public int getAcertos(){ return acertos; }
    public int getTotal_questoes(){ return total_questoes; }
    public int getPontuacao(){ return pontuacao; }
    public String getData(){ return data; }
    public String getHorario(){ return horario; }
}
