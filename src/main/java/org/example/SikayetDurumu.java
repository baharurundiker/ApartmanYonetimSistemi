package org.example;

public interface SikayetDurumu {
    String getDurum();
}

class Inceleniyor implements SikayetDurumu {
    @Override
    public String getDurum() {
        return "İnceleniyor";
    }
}

class Cozuldu implements SikayetDurumu {
    @Override
    public String getDurum() {
        return "Çözüldü";
    }
}
