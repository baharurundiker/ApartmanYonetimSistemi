package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OdemeYapKomutu implements Komut {

    private Kullanici kullanici;

    @FXML
    private Label odemeIdLabel;

    @FXML
    private Label kullaniciAdLabel;

    @FXML
    private Button odemeYapButton;

    @FXML
    private ListView<String> odemeListView;


    public OdemeYapKomutu(Kullanici kullanici) {
        this.kullanici = kullanici;
    }


    @FXML
    public void initialize() {
        odemeIdLabel.setText("Kullanıcı: " + kullanici.getIsim());
        kullaniciAdLabel.setText("Ödemeler:");


        loadOdemeler();
        odemeYapButton.setOnAction(e -> odemeYap());
    }

    // Kullanıcının ödemelerini veritabanından alır
    private void loadOdemeler() {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        List<String> odemeList = new ArrayList<>();

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT OdemeID, Tutar FROM odemeler WHERE KullaniciID = ? AND Durum = 'Ödenmedi'";
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, kullanici.getId());
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int odemeId = resultSet.getInt("OdemeID");
                double odemeTutari = resultSet.getDouble("Tutar");
                odemeList.add("Ödeme ID: " + odemeId + " | Tutar: " + odemeTutari);
            }

            odemeListView.getItems().setAll(odemeList); // ListView'e ödeme bilgilerini ekle

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void odemeYap() {
        String selectedOdeme = odemeListView.getSelectionModel().getSelectedItem();

        if (selectedOdeme == null) {
            // Eğer ödeme seçilmediyse kullanıcıyı uyar
            System.out.println("Lütfen ödeme seçin.");
            return;
        }

        String[] odemeDetails = selectedOdeme.split("\\|");
        int odemeId = Integer.parseInt(odemeDetails[0].trim().replace("Ödeme ID: ", ""));


        Connection connection = null;
        PreparedStatement updateStatement = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();

            // Ödeme durumunu 'ödendi' olarak güncelle
            String updateQuery = "UPDATE odemeler SET Durum = 'Ödendi', OdemeTarihi = ? WHERE OdemeID = ? AND KullaniciID = ? AND Durum = 'Ödenmedi'";
            updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            updateStatement.setInt(2, odemeId);
            updateStatement.setInt(3, kullanici.getId());

            int updatedRows = updateStatement.executeUpdate();

            if (updatedRows > 0) {
                System.out.println("Ödeme başarıyla tamamlandı: " + kullanici.getIsim());
                loadOdemeler(); // Listeyi yeniden yükle
            } else {
                System.out.println("Ödeme bulunamadı veya zaten ödenmiş: " + kullanici.getIsim());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (updateStatement != null) updateStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void showOdemeWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OdemeYap.fxml"));
            loader.setControllerFactory(controllerClass -> new OdemeYapKomutu(kullanici));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ödeme Yap");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void calistir() {
        showOdemeWindow(); // Ödeme penceresini açar
    }
}
