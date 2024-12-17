package org.example;

public class Beklemede implements TalepDurumu {
    @Override
    public void durumuGuncelle(Talep talep) {
        // "Beklemede" durumundan "Tamamlandı" durumuna geçiş yapalım.
        talep.setDurum(new Tamamlandi());
        System.out.println("Talep 'Beklemede' durumundan 'Tamamlandı' durumuna geçti.");
    }
}
