package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.*;

public class DuyuruYapKomutu implements Komut {
    private Kullanici kullanici;
    private BildirimYoneticisi bildirimYoneticisi;

    @FXML
    private Button duyuruButton;

    @FXML
    private TextField duyuruBaslikField;
    @FXML
    private TextArea duyuruIcerikArea;


    public DuyuruYapKomutu(Kullanici kullanici, BildirimYoneticisi bildirimYoneticisi) {
        this.kullanici = kullanici;
        this.bildirimYoneticisi = bildirimYoneticisi;
    }

    @FXML
    public void initialize() {
        duyuruButton.setOnAction(event -> {

            String duyuruBasligi = duyuruBaslikField.getText();
            String duyuruIcerigi = duyuruIcerikArea.getText();


            if (duyuruBasligi.isEmpty() || duyuruIcerigi.isEmpty()) {
                showAlert("Hata", "Duyuru başlık ve içeriği boş olamaz.", Alert.AlertType.ERROR);
                return;
            }

            try {

                insertDuyuru(duyuruBasligi, duyuruIcerigi);
            } catch (Exception e) {
                showAlert("Hata", "Duyuru gönderilirken bir hata oluştu.", Alert.AlertType.ERROR);
            }
        });
    }

    private void insertDuyuru(String baslik, String icerik) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/apartmanyonetim", "root", "");


            String selectQuery = "SELECT MAX(DuyuruID) FROM duyurular";
            stmt = conn.prepareStatement(selectQuery);
            rs = stmt.executeQuery();

            int yeniDuyuruId = 1;  // Varsayılan olarak 1, eğer tablo boşsa
            if (rs.next()) {
                yeniDuyuruId = rs.getInt(1) + 1;  // Son ID'yi al ve 1 artır
            }


            String insertQuery = "INSERT INTO duyurular (DuyuruID, Baslik, Icerik) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(insertQuery);
            stmt.setInt(1, yeniDuyuruId);
            stmt.setString(2, baslik);
            stmt.setString(3, icerik);
            stmt.executeUpdate();

            // Bildirim gönder
            String mesaj = "Yeni duyuru: " + baslik + "\n" + icerik;
            bildirimYoneticisi.bildirimGonder(mesaj); // Tüm gözlemcilere bildirim gönder


            showSuccessMessageAndReturn();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Duyuru gönderilirken bir hata oluştu.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showSuccessMessageAndReturn() {
        showAlert("Başarılı", "Duyuru başarıyla gönderildi.", Alert.AlertType.INFORMATION);
        Stage stage = (Stage) duyuruButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message);
        alert.setTitle(title);
        alert.showAndWait();
    }

    @Override
    public void calistir() {
        showDuyuruYapWindow();
    }

    public void showDuyuruYapWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DuyuruOlustur.fxml"));
            loader.setControllerFactory(controllerClass -> new DuyuruYapKomutu(kullanici, bildirimYoneticisi));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Duyuru Yap");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Duyuru yapma ekranı yüklenirken hata oluştu.", Alert.AlertType.ERROR);
        }
    }
}
