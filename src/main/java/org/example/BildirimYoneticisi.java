package org.example;

import java.util.ArrayList;
import java.util.List;


class BildirimYoneticisi {
    private List<Gozlemci> gozlemciler = new ArrayList<>();

    public void gozlemciEkle(Gozlemci gozlemci) {
        gozlemciler.add(gozlemci);
    }

    public void bildirimGonder(String mesaj) {
        for (Gozlemci gozlemci : gozlemciler) {
            gozlemci.guncelle(mesaj);
        }
    }
}