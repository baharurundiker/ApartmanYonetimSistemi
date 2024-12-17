package org.example;

import java.sql.Timestamp;

public class Daire {
    private int daireID;
    private int daireNumarasi;
    private int katNumarasi;
    private DaireDurumu durum;

    public Daire(int daireID, int daireNumarasi, int katNumarasi, DaireDurumu durum) {
        this.daireID = daireID;
        this.daireNumarasi = daireNumarasi;
        this.katNumarasi = katNumarasi;
        this.durum = durum;
    }

    public int getDaireID() {
        return daireID;
    }

    public int getDaireNumarasi() {
        return daireNumarasi;
    }

    public int getKatNumarasi() {
        return katNumarasi;
    }

    public DaireDurumu getDurum() {
        return durum;
    }

    public void setDurum(DaireDurumu durum) {
        this.durum = durum;
    }

    public void daireBilgileriniGoster() {
        System.out.println("DaireID: " + daireID + ", Daire Numarası: " + daireNumarasi +
                ", Kat: " + katNumarasi + ", Durum: " + durum.durumBilgisi());
    }
}
