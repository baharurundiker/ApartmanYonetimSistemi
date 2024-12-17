package org.example;


public interface OdemeDurumu {
    void durumuGuncelle(Odeme odeme);
}


class Odenmedi implements OdemeDurumu {
    @Override
    public void durumuGuncelle(Odeme odeme) {
        System.out.println("Ödeme durumu 'Ödenmedi' olarak güncelleniyor.");
        odeme.setDurum(new Odendi());  // Durum değişimi: 'Ödendi'
    }
}

class Odendi implements OdemeDurumu {
    @Override
    public void durumuGuncelle(Odeme odeme) {
        System.out.println("Ödeme durumu 'Ödendi' olarak güncelleniyor.");
    }
}
