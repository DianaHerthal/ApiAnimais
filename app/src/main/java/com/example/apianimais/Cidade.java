package com.example.apianimais;

public class Cidade {
    private int id;
    private String nome;
    private String ddd;

    public Cidade() {}

    public Cidade(int id, String nome, String ddd) {
        this.id = id;
        this.nome = nome;
        this.ddd = ddd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }
}
