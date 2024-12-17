package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.sql.*;
import java.util.List;

public class SikayetDuzenleKomutu implements Komut {
    private Yonetici yonetici;

    @FXML
    private ListView<Sikayet> sikayetListView;

    @FXML
    private Button updateStatusButton;

    public SikayetDuzenleKomutu(Yonetici yonetici) {
        this.yonetici = yonetici;
    }

    @FXML
    public void initialize() {
        loadComplaints();

        updateStatusButton.setOnAction(event -> {
            Sikayet selectedComplaint = sikayetListView.getSelectionModel().getSelectedItem();

            if (selectedComplaint == null) {
                showAlert("Hata", "Lütfen güncellemek için bir şikayet seçin.", Alert.AlertType.ERROR);
                return;
            }

            // Durumu güncelleme işlemi
            selectedComplaint.durumuGuncelle();

            // Listeyi yenile
            loadComplaints();

            // Başarı mesajı
            showAlert("Başarılı", "Şikayet durumu başarıyla güncellendi.", Alert.AlertType.INFORMATION);
        });
    }

    private void loadComplaints() {
        List<Sikayet> sikayetler = Sikayet.listele();
        sikayetListView.getItems().clear();
        sikayetListView.getItems().addAll(sikayetler);

        sikayetListView.setCellFactory(param -> new ListCell<Sikayet>() {
            @Override
            protected void updateItem(Sikayet sikayet, boolean empty) {
                super.updateItem(sikayet, empty);
                if (empty || sikayet == null) {
                    setText(null);
                } else {
                    String cellText = "Konu: " + sikayet.getKonu() + "\n" +
                            "Durum: " + sikayet.getDurum().getDurum() + "\n" +
                            "Açıklama: " + sikayet.getAciklama() + "\n" +
                            "Kullanıcı ID: " + sikayet.getKullaniciId();
                    setText(cellText);
                }
            }
        });
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
        showSikayetDuzenleWindow();
    }

    public void showSikayetDuzenleWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SikayetDuzenle.fxml"));
            loader.setControllerFactory(controllerClass -> new SikayetDuzenleKomutu(yonetici));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Şikayet Düzenle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Şikayet düzenleme penceresi yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }
}
