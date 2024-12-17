package org.example;

public class ConcreteDaireFactory implements DaireFactory {
    @Override
    public Daire daireOlustur(int daireID, int daireNumarasi, int katNumarasi, String durum) {
        DaireDurumu daireDurumu = durum.equalsIgnoreCase("Dolu") ? new DaireDurumu.DoluDaire() : new DaireDurumu.BosDaire();
        return new Daire(daireID, daireNumarasi, katNumarasi, daireDurumu);
    }
}
