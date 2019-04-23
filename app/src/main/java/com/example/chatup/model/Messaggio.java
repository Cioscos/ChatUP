package com.example.chatup.model;

public class Messaggio {
    private String messaggio, autore;

    public Messaggio(String messaggio, String autore) {
        this.messaggio = messaggio;
        this.autore = autore;
    }

    public Messaggio() {

    }

    public String getMessaggio() {
        return messaggio;
    }

    public String getAutore() {
        return autore;
    }
}
