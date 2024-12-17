package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;

public class TalepDuzenleKomutu implements Komut {
    private Yonetici yonetici;

    @FXML
    private ListView<Talep> talepListView;
    @FXML
    private Button updateStatusButton;

    public TalepDuzenleKomutu(Yonetici yonetici) {
        this.yonetici = yonetici;
    }

    @FXML
    public void initialize() {
        loadPreviousRequests();


        talepListView.setCellFactory(param -> new ListCell<Talep>() {
            @Override
            protected void updateItem(Talep talep, boolean empty) {
                super.updateItem(talep, empty);
                if (empty || talep == null) {
                    setText(null);
                } else {

                    String displayText = "Kullanıcı ID: " + talep.getKullaniciId() +
                            ", Talep Tarihi: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(talep.getTalepTarihi()) +
                            ", Açıklama: " + talep.getAciklama() +
                            ", Durum: " + talep.getDurum().getClass().getSimpleName();
                    setText(displayText);
                }
            }
        });

        updateStatusButton.setOnAction(event -> {
            Talep selectedRequest = talepListView.getSelectionModel().getSelectedItem();

            if (selectedRequest == null) {
                showAlert("Hata", "Lütfen güncellemek için bir talep seçin.", Alert.AlertType.ERROR);
                return;
            }


            selectedRequest.durumuGuncelle(); // Durum güncelleme


            updateRequestStatusInDatabase(selectedRequest);


            loadPreviousRequests();


            showAlert("Başarılı", "Talep durumu başarıyla güncellendi.", Alert.AlertType.INFORMATION);
        });
    }


    private void updateRequestStatusInDatabase(Talep talep) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "UPDATE bakimtalepleri SET Durum = ? WHERE KullaniciID = ?";
            preparedStatement = connection.prepareStatement(query);


            String yeniDurum = talep.getDurum() instanceof Beklemede ? "Beklemede" : "Tamamlandı";
            preparedStatement.setString(1, yeniDurum);
            preparedStatement.setInt(2, talep.getKullaniciId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Talep durumu güncellenirken bir hata oluştu.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPreviousRequests() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT k.AdSoyad, b.TalepTarihi, b.Aciklama, b.Durum, b.KullaniciID FROM bakimtalepleri b " +
                    "JOIN kullanicilar k ON b.KullaniciID = k.KullaniciID";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            talepListView.getItems().clear();

            while (resultSet.next()) {
                String adSoyad = resultSet.getString("AdSoyad");
                Timestamp talepTarihi = resultSet.getTimestamp("TalepTarihi");
                String aciklama = resultSet.getString("Aciklama");
                String durumString = resultSet.getString("Durum");
                int kullaniciID = resultSet.getInt("KullaniciID");


                TalepDurumu durum = "Beklemede".equals(durumString) ? new Beklemede() : new Tamamlandi();

                Talep talep = new Talep(kullaniciID, talepTarihi, aciklama, durum);
                talepListView.getItems().add(talep);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void calistir() {

        showTalepDuzenleWindow();
    }

    public void showTalepDuzenleWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TalepDuzenle.fxml"));
            loader.setControllerFactory(controllerClass -> new TalepDuzenleKomutu(yonetici));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Talep Düzenle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Talep düzenleme penceresi yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }
}
