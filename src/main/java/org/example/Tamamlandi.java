package org.example;

public class Tamamlandi implements TalepDurumu {
    @Override
    public void durumuGuncelle(Talep talep) {

        System.out.println("Talep zaten 'Tamamlandı' durumunda.");
    }
}
