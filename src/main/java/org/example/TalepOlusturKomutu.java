package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;

public class TalepOlusturKomutu implements Komut {
    @FXML private TextArea aciklamaTextArea;
    @FXML private Button olusturButton;
    @FXML private ListView<String> talepListView;
    private Kullanici kullanici;
    public TalepOlusturKomutu(Kullanici kullanici) {
        this.kullanici = kullanici;
    }
    @FXML
    public void initialize() {
        loadPreviousComplaints();
        olusturButton.setOnAction(event -> {


            String aciklama = aciklamaTextArea.getText();

            if (aciklama.isEmpty()) {
                System.out.println("Lütfen tüm alanları doldurun.");
                return;
            }
            insertRequest(aciklama);

        });
    }

    public void showTalepOlusturWindow() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TalepOlustur.fxml"));


            loader.setControllerFactory(controllerClass -> new TalepOlusturKomutu(kullanici));


            Parent root = loader.load();


            Stage stage = new Stage();
            stage.setTitle("Talep Oluştur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
    public void showSuccessMessageAndReturn() {
        showAlert("Başarılı", "Talep başarıyla oluşturuldu.", Alert.AlertType.INFORMATION);


        Stage stage = (Stage) olusturButton.getScene().getWindow();
        stage.close();
    }
    public void loadPreviousComplaints() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT TalepTarihi,Aciklama, Durum FROM bakimtalepleri WHERE KullaniciID = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, kullanici.getId());
            resultSet = preparedStatement.executeQuery();


            talepListView.getItems().clear();

            while (resultSet.next()) {
                Timestamp tarih = resultSet.getTimestamp("TalepTarihi");
                String aciklama = resultSet.getString("Aciklama");
                String durum = resultSet.getString("Durum");


                String complaintInfo = "Tarih: " + tarih + "\nAçıklama: " + aciklama + "\nDurum: " + durum;
                talepListView.getItems().add(complaintInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Talepler yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }







    public void insertRequest(String aciklama) {
        String durum = "Beklemede";

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();


            String selectQuery = "SELECT MAX(TalepID) FROM bakimtalepleri";
            selectStatement = connection.prepareStatement(selectQuery);
            resultSet = selectStatement.executeQuery();

            int newTalepID = 1;
            if (resultSet.next()) {
                newTalepID = resultSet.getInt(1) + 1;
            }

            String insertQuery = "INSERT INTO bakimtalepleri (TalepID, KullaniciID,Aciklama, TalepTarihi, Durum) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insertQuery);
            statement.setInt(1, newTalepID); // TalepID
            statement.setInt(2, kullanici.getId()); // KullaniciID
            statement.setString(3, aciklama); // Aciklama
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // TalepTarihi
            statement.setString(5, durum); // Durum


            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Talep başarıyla oluşturuldu. ID: " + newTalepID);
                showSuccessMessageAndReturn();
            }

        } catch (SQLException e) {
            e.printStackTrace();
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


    @Override
    public void calistir() {
     showTalepOlusturWindow();
    }
}

