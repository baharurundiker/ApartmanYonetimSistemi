package org.example;

public class Yonetici extends Kullanici {
    private Komut duyuruYapKomutu;
    private Komut talepDuzenleKomutu;
    private Komut odemeOlusturKomutu;
    private Komut odemeGorKomutu;
    private Komut kullaniciSilKomutu;
    private Komut sikayetDuzenleKomutu;
    private Komut daireGuncelleKomutu;



    public Yonetici(int id) {
        super(id);
    }


    public void setDuyuruYapKomutu(Komut duyuruYapKomutu) {
        this.duyuruYapKomutu = duyuruYapKomutu;
    }

    public void setTalepDuzenleKomutu(Komut talepDuzenleKomutu) {
        this.talepDuzenleKomutu = talepDuzenleKomutu;
    }

    public void setOdemeOlusturKomutu(Komut odemeOlusturKomutu) {
        this.odemeOlusturKomutu = odemeOlusturKomutu;
    }

    public void setOdemeGorKomutu(Komut odemeGorKomutu) {
        this.odemeGorKomutu = odemeGorKomutu;
    }

    public void setKullaniciSilKomutu(Komut kullaniciSilKomutu) {
        this.kullaniciSilKomutu = kullaniciSilKomutu;
    }

    public void setSikayetDuzenleKomutu(Komut sikayetDuzenleKomutu) {
        this.sikayetDuzenleKomutu = sikayetDuzenleKomutu;
    }

    public void setDaireGuncelleKomutu(Komut daireGuncelleKomutu) {
        this.daireGuncelleKomutu = daireGuncelleKomutu;
    }


    public void duyuruYap() {
        if (duyuruYapKomutu != null) duyuruYapKomutu.calistir();
    }

    public void talepDuzenle() {
        if (talepDuzenleKomutu != null) talepDuzenleKomutu.calistir();
    }

    public void odemeOlustur() {
        if (odemeOlusturKomutu != null) odemeOlusturKomutu.calistir();
    }

    public void odemeGor() {
        if (odemeGorKomutu != null) odemeGorKomutu.calistir();
    }

    public void kullaniciSil() {
        if (kullaniciSilKomutu != null) kullaniciSilKomutu.calistir();
    }

    public void sikayetDuzenle() {
        if (sikayetDuzenleKomutu != null) sikayetDuzenleKomutu.calistir();
    }

    public void daireGuncelle() {
        if (daireGuncelleKomutu != null) daireGuncelleKomutu.calistir();
    }
}
