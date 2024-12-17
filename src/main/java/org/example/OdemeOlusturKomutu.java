package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;

public class OdemeOlusturKomutu implements Komut {
    private Kullanici kullanici;

    @FXML
    private TextField tutarfield;
    @FXML
    private TextField idfield;
    @FXML
    private TextArea aciklamaTextArea;
    @FXML
    private Button olusturButton;
    @FXML
    private ListView odemeListView;

    public OdemeOlusturKomutu(Kullanici kullanici) {
        this.kullanici = kullanici;
    }
    public void loadPreviousPayments() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT k.AdSoyad, o.OdemeTarihi, o.Aciklama, o.Durum FROM odemeler o " +
                    "JOIN kullanicilar k ON o.KullaniciID = k.KullaniciID";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            odemeListView.getItems().clear();

            while (resultSet.next()) {

                Timestamp odemeTarihi = resultSet.getTimestamp("OdemeTarihi");


                if (odemeTarihi != null) {
                    String adSoyad = resultSet.getString("AdSoyad");
                    String aciklama = resultSet.getString("Aciklama");
                    String durum = resultSet.getString("Durum");


                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String formattedDate = sdf.format(odemeTarihi);


                    String odemeInfo = "Ad Soyad: " + adSoyad + "\nÖdeme Tarihi: " + formattedDate + "\nAçıklama: " + aciklama + "\nDurum: " + durum;
                    odemeListView.getItems().add(odemeInfo);
                } else {

                    String adSoyad = resultSet.getString("AdSoyad");
                    String aciklama = resultSet.getString("Aciklama");
                    String durum = resultSet.getString("Durum");

                    String odemeInfo = "Ad Soyad: " + adSoyad + "\nÖdeme Tarihi: Veritabanında bulunamadı\nAçıklama: " + aciklama + "\nDurum: " + durum;
                    odemeListView.getItems().add(odemeInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Ödemeler yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
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







    @FXML
    public void initialize() {
        loadPreviousPayments();

        olusturButton.setOnAction(event -> {

            String tutarStr = tutarfield.getText();
            String kullaniciIDStr = idfield.getText();
            String aciklama = aciklamaTextArea.getText();


            if (tutarStr.isEmpty() || kullaniciIDStr.isEmpty() || aciklama.isEmpty()) {
                showAlert("Hata", "Lütfen tüm alanları doldurun.", Alert.AlertType.ERROR);
                return;
            }

            try {

                double tutar = Double.parseDouble(tutarStr);
                int kullaniciID = Integer.parseInt(kullaniciIDStr);


                insertPayment(tutar, kullaniciID, aciklama);
            } catch (NumberFormatException e) {

                showAlert("Hata", "Tutar ve Kullanıcı ID geçerli bir sayı olmalıdır.", Alert.AlertType.ERROR);
            }
        });
    }

    private void insertPayment(double tutar, int kullaniciID, String aciklama) {
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement selectStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();

            String selectQuery = "SELECT MAX(OdemeID) FROM odemeler";
            selectStatement = connection.prepareStatement(selectQuery);
            resultSet = selectStatement.executeQuery();

            int newOdemeID = 1;
            if (resultSet.next()) {
                newOdemeID = resultSet.getInt(1) + 1;
            }

            String query = "INSERT INTO odemeler (OdemeID,KullaniciID, Tutar, Durum, Aciklama) VALUES (?,?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, newOdemeID);
            statement.setInt(2, kullaniciID);
            statement.setDouble(3, tutar);
            statement.setString(4, "Ödenmedi");
            statement.setString(5, aciklama);

            statement.executeUpdate();
            System.out.println("Ödeme başarıyla oluşturuldu.");
            showSuccessMessageAndReturn();

            loadPreviousPayments();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Hata", "Ödeme oluşturulurken bir hata oluştu.", Alert.AlertType.ERROR);
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showSuccessMessageAndReturn() {
        showAlert("Başarılı", "Ödeme başarıyla oluşturuldu.", Alert.AlertType.INFORMATION);
        Stage stage = (Stage) olusturButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message);
        alert.setTitle(title);
        alert.showAndWait();
    }

    @Override
    public void calistir() {
        showOdemeOlusturWindow();
    }

    public void showOdemeOlusturWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OdemeOlustur.fxml"));
            loader.setControllerFactory(controllerClass -> new OdemeOlusturKomutu(kullanici));

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ödeme Oluştur");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Ödeme oluşturma ekranı yüklenirken hata oluştu.", Alert.AlertType.ERROR);
        }
}}
