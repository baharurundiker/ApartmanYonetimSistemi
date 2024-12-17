package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

public class Talep {

    private int kullaniciId;
    private Timestamp talepTarihi;
    private String aciklama;
    private TalepDurumu durum; // Durum objesi
    private int kullaniciID;

    public Talep(int kullaniciId, Timestamp talepTarihi, String aciklama, TalepDurumu durum) {

        this.kullaniciId = kullaniciId;
        this.talepTarihi = talepTarihi;
        this.aciklama = aciklama;
        this.durum = durum;
    }

    public void setDurum(TalepDurumu durum) {
        this.durum = durum;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void durumuGuncelle() {
        Connection connection = null;
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();


            String selectQuery = "SELECT Durum FROM bakimtalepleri WHERE KullaniciID = ?";
            selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, this.kullaniciId);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String mevcutDurum = resultSet.getString("Durum");
                String yeniDurum = "Beklemede".equals(mevcutDurum) ? "Tamamlandı" : "Beklemede";


                String updateQuery = "UPDATE bakimtalepleri SET Durum = ? WHERE KullaniciID = ?";
                updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, yeniDurum);
                updateStatement.setInt(2, this.kullaniciId);

                int updatedRows = updateStatement.executeUpdate();
                if (updatedRows > 0) {

                    this.durum = "Beklemede".equals(yeniDurum) ? new Beklemede() : new Tamamlandi();
                }
            } else {
                showAlert("Hata", "Veritabanında talep bulunamadı.", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Veritabanı hatası oluştu: " + e.getMessage(), Alert.AlertType.ERROR);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (selectStatement != null) selectStatement.close();
                if (updateStatement != null) updateStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }





    public int getKullaniciId() {
        return kullaniciId;
    }

    public Timestamp getTalepTarihi() {
        return talepTarihi;
    }

    public String getAciklama() {
        return aciklama;
    }

    public TalepDurumu getDurum() {
        return durum;
    }

    public static List<Talep> listele() {
        List<Talep> talepler = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT * FROM bakimtalepleri";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int kullaniciId = resultSet.getInt("KullaniciID");
                Timestamp talepTarihi = resultSet.getTimestamp("TalepTarihi");
                String aciklama = resultSet.getString("Aciklama");
                String durumString = resultSet.getString("Durum");

                TalepDurumu durum;
                if ("Beklemede".equals(durumString)) {
                    durum = new Beklemede();
                } else if ("Tamamlandı".equals(durumString)) {
                    durum = new Tamamlandi();
                } else {

                    durum = new Beklemede();
                }

                Talep talep = new Talep(kullaniciId, talepTarihi, aciklama, durum);
                talepler.add(talep);
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
        return talepler;
    }
}
