package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {

        while (true) {
            String email = emailField.getText();
            String password = passwordField.getText();


            if (email.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Boş Alan");
                alert.setHeaderText(null);
                alert.setContentText("Lütfen tüm alanları doldurun.");
                alert.showAndWait();
                break;
            }


            Kullanici kullanici = DatabaseBaglanti.kullaniciGirisi(email, password);

            if (kullanici != null) {

                if (kullanici instanceof Yonetici) {
                    YoneticiEkrani yoneticiEkrani = new YoneticiEkrani((Stage) emailField.getScene().getWindow(),kullanici);
                    yoneticiEkrani.show();
                } else {
                    SakinEkrani sakinEkrani = new SakinEkrani((Stage) emailField.getScene().getWindow(), kullanici);
                    sakinEkrani.show();
                }
                break;
            } else {

                showErrorMessage("Geçersiz email veya şifre!");
                break;
            }
        }
    }

    @FXML
    public void handleRegister() {

        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegisterScreen.fxml"));
            Stage registerStage = new Stage();
            registerStage.setScene(new Scene(loader.load()));
            registerStage.setTitle("Kayıt Ol");
            registerStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Giriş Hatası");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
