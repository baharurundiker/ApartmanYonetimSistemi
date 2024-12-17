package org.example;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class SakinEkrani {
    private Stage stage;  // Ekran penceresi
    private Kullanici kullanici;  // Giriş yapan sakin (Kullanıcı)

    @FXML
    private Label hosgeldinLabel; // Hoşgeldin mesajı

    @FXML
    private Label daireBilgisiLabel; // Daire bilgisi
    @FXML
    private Button duyuruListesiGorButton;

    @FXML
    private Button odemeYapButton; // Ödeme Yapma Butonu

    @FXML
    private Button sikayetOlusturButton; // Şikayet Oluştur Butonu

    @FXML
    private Button talepOlusturButton; // Talep Oluştur Butonu


    public SakinEkrani(Stage stage, Kullanici kullanici) {
        this.stage = stage;
        this.kullanici = kullanici;


        hosgeldinLabel = new Label("Hoşgeldin, " + kullanici.getIsim());
        daireBilgisiLabel = new Label("Daire bilgilerinizi buradan görebilirsiniz.");


        VBox root = new VBox(10, hosgeldinLabel, daireBilgisiLabel);
        kullaniciVeDaireBilgileriniGetir();


        odemeYapButton = new Button("Ödeme Yap");
        odemeYapButton.setOnAction(e -> odemeYap());

        sikayetOlusturButton = new Button("Şikayet Oluştur");
        sikayetOlusturButton.setOnAction(e -> sikayetOlustur());

        talepOlusturButton = new Button("Talep Oluştur");
        talepOlusturButton.setOnAction(e -> talepOlustur());

        duyuruListesiGorButton = new Button("Duyuruları Gör");
        duyuruListesiGorButton.setOnAction(e -> duyuruGor());


        root.getChildren().addAll(odemeYapButton, sikayetOlusturButton, talepOlusturButton,duyuruListesiGorButton);


        stage.setScene(new Scene(root, 400, 300));
    }


    public void show() {
        stage.setTitle("Sakin Ekranı");
        stage.show();
    }


    private void kullaniciVeDaireBilgileriniGetir() {
        if (kullanici == null) {
            System.out.println("Hata: Kullanıcı bilgisi bulunamadı!");
            return;
        }

        String query = "SELECT k.AdSoyad, d.DaireNumarasi, d.KatNumarasi, d.Durum " +
                "FROM kullanicilar k " +
                "JOIN daireler d ON k.DaireID = d.DaireID " +
                "WHERE k.KullaniciID = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/apartmanyonetim", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, kullanici.getId());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String adsoyad = rs.getString("AdSoyad");
                int daireNumarasi = rs.getInt("DaireNumarasi");
                int katNumarasi = rs.getInt("KatNumarasi");
                String durum = rs.getString("Durum");

                hosgeldinLabel.setText("Hoşgeldin, " + adsoyad);
                daireBilgisiLabel.setText("Daire Bilgileriniz: " + daireNumarasi + " numaralı daire, " +
                        katNumarasi + ". kat, Durum: " + durum);
            } else {
                hosgeldinLabel.setText("Kullanıcı bilgisi bulunamadı.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }
    }


    @FXML
    private void odemeYap() {
        OdemeYapKomutu odemeYapKomutu = new OdemeYapKomutu(kullanici);
        this.kullanici.veritabaniGetir();
        odemeYapKomutu.calistir();
    }
    @FXML
    private void duyuruGor() {

        DuyuruListesiGorKomutu duyuruListesiGorKomutu = new DuyuruListesiGorKomutu();
        duyuruListesiGorKomutu.calistir();

    }


    @FXML
    public void sikayetOlustur() {
        SikayetOlusturKomutu sikayetOlusturKomutu = new SikayetOlusturKomutu(kullanici);
        sikayetOlusturKomutu.calistir();
    }



    @FXML
    public void talepOlustur() {
         TalepOlusturKomutu talepOlusturKomutu = new TalepOlusturKomutu(kullanici);
         talepOlusturKomutu.calistir();
    }
}
