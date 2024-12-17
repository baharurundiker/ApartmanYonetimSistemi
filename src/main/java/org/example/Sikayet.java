package org.example;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Sikayet {
    private int kullaniciId;
    private int sikayetId;
    private String konu;
    private String aciklama;
    private Timestamp sikayetTarihi;
    private SikayetDurumu durum;  // Durum objesi (State Pattern)

    public Sikayet(int sikayetId, int kullaniciId, String konu, String aciklama, Timestamp sikayetTarihi, SikayetDurumu durum) {
        this.sikayetId = sikayetId;
        this.kullaniciId = kullaniciId;
        this.konu = konu;
        this.aciklama = aciklama;
        this.sikayetTarihi = sikayetTarihi;
        this.durum = durum;
    }

    public void durumuGuncelle() {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String yeniDurum = durum instanceof Inceleniyor ? "Çözüldü" : "İnceleniyor";

            String query = "UPDATE sikayetler SET Durum = ? WHERE SikayetID = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, yeniDurum);
            statement.setInt(2, this.sikayetId);

            int updatedRows = statement.executeUpdate();
            if (updatedRows > 0) {
                this.durum = yeniDurum.equals("Çözüldü") ? new Cozuldu() : new Inceleniyor();
            } else {
                showAlert("Hata", "Şikayet bulunamadı.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Veritabanı hatası oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Sikayet> listele() {
        List<Sikayet> sikayetler = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT * FROM sikayetler";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int sikayetId = resultSet.getInt("SikayetID");
                int kullaniciId = resultSet.getInt("KullaniciID");
                String konu = resultSet.getString("Konu");
                String aciklama = resultSet.getString("Aciklama");
                Timestamp sikayetTarihi = resultSet.getTimestamp("SikayetTarihi");
                String durumString = resultSet.getString("Durum");

                SikayetDurumu durum = "İnceleniyor".equals(durumString) ? new Inceleniyor() : new Cozuldu();
                sikayetler.add(new Sikayet(sikayetId, kullaniciId, konu, aciklama, sikayetTarihi, durum));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sikayetler;
    }

    // Getter ve Setter metodları
    public SikayetDurumu getDurum() {
        return durum;
    }

    public int getSikayetId() {
        return sikayetId;
    }

    public String getKonu() {
        return konu;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public String getAciklama() {
        return aciklama;
    }

    public int getKullaniciId() {
        return kullaniciId;
    }

}
