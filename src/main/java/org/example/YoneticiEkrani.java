package org.example;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class YoneticiEkrani {

    private Stage stage;
    private Yonetici yonetici;


    @FXML
    private Label hosgeldinLabel;
    @FXML
    private Button duyuruYapButton;
    @FXML
    private Button talepDuzenleButton;
    @FXML
    private Button odemeOlusturButton;
    @FXML
    private Button kullaniciSilButton;
    @FXML
    private Button sikayetDuzenleButton;
    @FXML
    private Button daireGuncelleButton;

    public YoneticiEkrani(Stage stage, Kullanici yonetici) {
        this.stage = stage;
        this.yonetici = (Yonetici) yonetici;
    }

    public void show() {

            hosgeldinLabel = new Label("Hoşgeldin, Yönetici");


        duyuruYapButton = new Button("Duyuru Yap");
        duyuruYapButton.setOnAction(e -> duyuruYap());

        talepDuzenleButton = new Button("Talep Düzenle");
        talepDuzenleButton.setOnAction(e -> talepDuzenle());

        odemeOlusturButton = new Button("Ödeme Oluştur");
        odemeOlusturButton.setOnAction(e -> odemeOlustur());

        kullaniciSilButton = new Button("Kullanıcı Sil");
        kullaniciSilButton.setOnAction(e -> kullaniciSil());

        sikayetDuzenleButton = new Button("Şikayet Düzenle");
        sikayetDuzenleButton.setOnAction(e -> sikayetDuzenle());

        daireGuncelleButton = new Button("Daire Güncelle");
        daireGuncelleButton.setOnAction(e -> daireGuncelle());


        VBox root = new VBox(10,
                hosgeldinLabel,
                duyuruYapButton,
                talepDuzenleButton,
                odemeOlusturButton,
                kullaniciSilButton,
                sikayetDuzenleButton,
                daireGuncelleButton
        );


        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Yönetici Ekranı");
        stage.show();
    }


    @FXML
    public void duyuruYap() {

        BildirimYoneticisi bildirimYoneticisi = new BildirimYoneticisi();


        DuyuruYapKomutu duyuruYapKomutu = new DuyuruYapKomutu(yonetici, bildirimYoneticisi);


        duyuruYapKomutu.calistir();
    }


    @FXML
    public void talepDuzenle() {
        TalepDuzenleKomutu talepDuzenleKomutu = new TalepDuzenleKomutu(yonetici);
        talepDuzenleKomutu.calistir();
    }

    @FXML
    public void odemeOlustur() {
        OdemeOlusturKomutu odemeOlusturKomutu = new OdemeOlusturKomutu(yonetici);
        odemeOlusturKomutu.calistir();
    }

    @FXML
    public void kullaniciSil() {
        KullaniciSilKomutu kullaniciSilKomutu = new KullaniciSilKomutu(yonetici);
        kullaniciSilKomutu.calistir();
    }

    @FXML
    public void sikayetDuzenle() {
        SikayetDuzenleKomutu sikayetDuzenleKomutu = new SikayetDuzenleKomutu(yonetici);
        sikayetDuzenleKomutu.calistir();
    }

    @FXML
    public void daireGuncelle() {
        DaireGuncelleKomutu daireGuncelleKomutu = new DaireGuncelleKomutu(yonetici);
        daireGuncelleKomutu.calistir();
    }
}
