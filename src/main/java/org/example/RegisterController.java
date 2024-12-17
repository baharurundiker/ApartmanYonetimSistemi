package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField apartmentNumberField; // Changed to apartment number

    public RegisterController() {
        // Constructor
    }

    @FXML
    public void initialize() {
        // Initialization code
    }

    @FXML
    public void handleRegister() {
        System.out.println("Kayıt butonuna tıklandı");

        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String apartmentNumber = apartmentNumberField.getText();


        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || apartmentNumber.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Eksik Bilgi");
            alert.setHeaderText(null);
            alert.setContentText("Lütfen tüm alanları doldurun.");
            alert.showAndWait();
            return;
        }


        String apartmentId = getApartmentIdFromNumber(apartmentNumber);
        if (apartmentId == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Geçersiz Daire Numarası");
            alert.setHeaderText(null);
            alert.setContentText("Geçersiz daire numarası. Lütfen doğru bir daire numarası giriniz.");
            alert.showAndWait();
            return;
        }


        int nextUserId = getNextUserId();


        if (registerUser(nextUserId, name, phone, email, password, apartmentId)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Başarıyla Kayıt Oldu");
            alert.setHeaderText(null);
            alert.setContentText("Kayıt başarılı! Giriş yapabilirsiniz.");
            alert.showAndWait();


            navigateToLogin();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kayıt Hatası");
            alert.setHeaderText(null);
            alert.setContentText("Kullanıcı kaydedilemedi. Lütfen tekrar deneyin.");
            alert.showAndWait();
        }
    }


    private int getNextUserId() {
        String query = "SELECT MAX(KullaniciID) AS max_id FROM kullanicilar";
        try (Connection conn = DatabaseBaglanti.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }


    private String getApartmentIdFromNumber(String apartmentNumber) {
        String query = "SELECT DaireID FROM daireler WHERE DaireNumarasi = ?";
        try (Connection conn = DatabaseBaglanti.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, apartmentNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("DaireID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean registerUser(int userId, String name, String phone, String email, String password, String apartmentId) {
        String query = "INSERT INTO kullanicilar (KullaniciID, AdSoyad, Telefon, Email, Sifre, DaireID, Rol) VALUES (?, ?, ?, ?, ?, ?, 'Kiracı')";

        try (Connection conn = DatabaseBaglanti.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setString(6, apartmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    public void handleCancel() {

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }


    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Giriş Yap");

            Stage currentStage = (Stage) nameField.getScene().getWindow();
            currentStage.close();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Yönlendirme Hatası");
            alert.setHeaderText(null);
            alert.setContentText("Giriş sayfasına yönlendirilirken bir hata oluştu.");
            alert.showAndWait();
        }
    }
}
