package com.example.apianimais;

public class Animal {
    private int id;
    private String descricao;
    private int idade;
    private String finalidade; // "D" or "A"
    private double valor; // associated value for donation
    private int idTipo;
    private int idRaca;
    private int idCidade;
    private String ddd;
    private Cidade cidade;
    private Raca raca;

    public Animal() {}

    public Animal(int id, String descricao, int idade, String finalidade, double valor, int idTipo, int idRaca, int idCidade, String ddd, Cidade cidade, Raca raca) {
        this.id = id;
        this.descricao = descricao;
        this.idade = idade;
        this.finalidade = finalidade;
        this.valor = valor;
        this.idTipo = idTipo;
        this.idRaca = idRaca;
        this.idCidade = idCidade;
        this.ddd = ddd;
        this.cidade = cidade;
        this.raca = raca;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(String finalidade) {
        this.finalidade = finalidade;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public int getIdRaca() {
        return idRaca;
    }

    public void setIdRaca(int idRaca) {
        this.idRaca = idRaca;
    }

    public int getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(int idCidade) {
        this.idCidade = idCidade;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Raca getRaca() {
        return raca;
    }

    public void setRaca(Raca raca) {
        this.raca = raca;
    }
}
