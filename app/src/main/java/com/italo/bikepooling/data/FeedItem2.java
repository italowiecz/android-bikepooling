package com.italo.bikepooling.data;

/**
 * Created by italo on 28/11/2017.
 */

public class FeedItem2 {
    private String nome, descricao, imagemRota, imagemProfile, timeStamp, data, hora, distancia, tempoEstimado;

    public FeedItem2() {
    }

    public FeedItem2(String nome, String descricao, String imagemRota, String imagemProfile, String timeStamp, String data, String hora, String distancia, String tempoEstimado) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagemRota = imagemRota;
        this.imagemProfile = imagemProfile;
        this.timeStamp = timeStamp;
        this.data = data;
        this.hora = hora;
        this.distancia = distancia;
        this.tempoEstimado = tempoEstimado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemRota() {
        return imagemRota;
    }

    public void setImagemRota(String imagemRota) {
        this.imagemRota = imagemRota;
    }

    public String getImagemProfile() {
        return imagemProfile;
    }

    public void setImagemProfile(String imagemProfile) {
        this.imagemProfile = imagemProfile;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(String tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }
}

