package org.example;

import java.sql.*;

public class Odeme {
    private int odemeId;
    private int kullaniciId;
    private double tutar;
    private OdemeDurumu durum;


    public Odeme(int odemeId) {
        this.odemeId = odemeId;
    }

    // Getter methods
    public int getOdemeId() {
        return odemeId;
    }

    public int getKullaniciId() {
        return kullaniciId;
    }

    public double getTutar() {
        return tutar;
    }

    public OdemeDurumu getDurum() {
        return durum;
    }

    @Override
    public String toString() {
        return "Ödeme ID: " + odemeId + ", Kullanıcı ID: " + kullaniciId + ", Tutar: " + tutar + ", Durum: " + durum.getClass().getSimpleName();
    }


    public void setDurum(OdemeDurumu durum) {
        this.durum = durum;
    }

    public void durumuGuncelle() {
        if (durum != null) {
            durum.durumuGuncelle(this);
        }
    }
}
