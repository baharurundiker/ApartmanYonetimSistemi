package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.sql.*;
import java.sql.Timestamp;

public class SikayetOlusturKomutu implements Komut {

    private Kullanici kullanici;

    @FXML private TextField konuTextField;
    @FXML private TextArea aciklamaTextArea;
    @FXML private Button olusturButton;
    @FXML private ListView<String> sikayetListView;


    public SikayetOlusturKomutu(Kullanici kullanici) {
        this.kullanici = kullanici;
    }

    @FXML
    public void initialize() {
        loadPreviousComplaints();
        olusturButton.setOnAction(event -> {
            String konu = konuTextField.getText();
            String aciklama = aciklamaTextArea.getText();

            if (konu.isEmpty() || aciklama.isEmpty()) {
                showAlert("Hata", "Lütfen tüm alanları doldurun.", AlertType.ERROR);
                return;
            }


            insertComplaint(konu, aciklama);
        });
    }


    public void showSikayetOlusturWindow() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SikayetOlustur.fxml"));
            loader.setControllerFactory(controllerClass -> new SikayetOlusturKomutu(kullanici));


            Parent root = loader.load();


            Stage stage = new Stage();
            stage.setTitle("Şikayet Oluştur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Şikayet oluşturma ekranı yüklenirken hata oluştu.", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public void showSuccessMessageAndReturn() {
        showAlert("Başarılı", "Şikayet başarıyla oluşturuldu.", AlertType.INFORMATION);


        Stage stage = (Stage) olusturButton.getScene().getWindow();
        stage.close();
    }

    public void loadPreviousComplaints() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT SikayetTarihi,Konu, Aciklama, Durum FROM sikayetler WHERE KullaniciID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, kullanici.getId());
            resultSet = preparedStatement.executeQuery();


            sikayetListView.getItems().clear();

            while (resultSet.next()) {
                Timestamp tarih = resultSet.getTimestamp("SikayetTarihi");
                String konu = resultSet.getString("Konu");
                String aciklama = resultSet.getString("Aciklama");
                String durum = resultSet.getString("Durum");


                String complaintInfo = "Tarih: " + tarih + "\nKonu: " + konu + "\nAçıklama: " + aciklama + "\nDurum: " + durum;
                sikayetListView.getItems().add(complaintInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Şikayetler yüklenirken bir hata oluştu.", AlertType.ERROR);
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

    @Override
    public void calistir() {
        showSikayetOlusturWindow();
    }


    private void insertComplaint(String konu, String aciklama) {
        String durum = "İnceleniyor";

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();


            String selectQuery = "SELECT MAX(SikayetID) FROM sikayetler";
            selectStatement = connection.prepareStatement(selectQuery);
            resultSet = selectStatement.executeQuery();

            int newSikayetID = 1;
            if (resultSet.next()) {
                newSikayetID = resultSet.getInt(1) + 1;
            }


            String insertQuery = "INSERT INTO sikayetler (SikayetID, KullaniciID, Konu, Aciklama, SikayetTarihi, Durum) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, newSikayetID);
            statement.setInt(2, kullanici.getId());
            statement.setString(3, konu);
            statement.setString(4, aciklama);
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.setString(6, durum);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                showSuccessMessageAndReturn();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Şikayet eklenirken bir hata oluştu.", AlertType.ERROR);
        } finally {
            try {
                if (statement != null) statement.close();
                if (selectStatement != null) selectStatement.close();
                if (resultSet != null) resultSet.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
