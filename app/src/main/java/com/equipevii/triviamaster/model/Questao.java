package com.equipevii.triviamaster.model;

public class Questao {
    private int id;
    private String categoria;
    private String dificuldade;
    private String enunciado;
    private String respostaCorreta;
    private String[] respostasErradas;

    public Questao(int id, String categoria, String dificuldade, String enunciado,
                   String respostaCorreta, String[] respostasErradas){
        this.id = id;
        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.enunciado = enunciado;
        this.respostaCorreta = respostaCorreta;
        this.respostasErradas = respostasErradas;
    }

    public int getId(){
        return id;
    }

    public String getCategoria(){
        return categoria;
    }

    public String getDificuldade(){
        return dificuldade;
    }

    public String getEnunciado(){
        return enunciado;
    }

    public String getRespostaCorreta(){
        return respostaCorreta;
    }
    public String[] getRespostasErradas(){
        return respostasErradas;
    }
}
