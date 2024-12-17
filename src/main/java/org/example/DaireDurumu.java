package org.example;

public interface DaireDurumu {
    String durumBilgisi();

    class BosDaire implements DaireDurumu {
        @Override
        public String durumBilgisi() {
            return "Boş";
        }
    }

    class DoluDaire implements DaireDurumu {
        @Override
        public String durumBilgisi() {
            return "Dolu";
        }
    }
}
