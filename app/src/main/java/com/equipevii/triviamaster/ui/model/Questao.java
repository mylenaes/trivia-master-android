package com.equipevii.triviamaster.ui.model;

import java.util.List;
public class Questao {
    private int id;
    private String categoria;
    private String dificuldade;
    private String enunciado;
    private String respostaCorreta;
    private List<String> respostasErradas;

    public Questao(int id, String categoria, String dificuldade, String enunciado, String respostaCorreta, List<String> respostasErradas){
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

    public List<String> getRespostasErradas(){
        return respostasErradas;
    }
}
