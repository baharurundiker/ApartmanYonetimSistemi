package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KullaniciSilKomutu extends Application implements Komut {

    @FXML
    private ListView<Kullanici> kullaniciListView;
    private Yonetici yonetici;

    private ObservableList<Kullanici> kullaniciObservableList;

    public KullaniciSilKomutu(Yonetici yonetici) {
        this.yonetici = yonetici;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    @Override
    public void calistir() {

        Stage primaryStage = new Stage();
        showKullaniciSil(primaryStage);
    }


    public void showKullaniciSil(Stage primaryStage) {
        primaryStage.setTitle("Kullanıcı Silme Arayüzü");

        List<Kullanici> kullanicilar = listeleKullanicilar();
        kullaniciObservableList = FXCollections.observableArrayList(kullanicilar);

        kullaniciListView = new ListView<>(kullaniciObservableList);
        kullaniciListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Button silButton = new Button("Seçili Kullanıcıyı Sil");
        silButton.setOnAction(event -> kullaniciSil()); // Silme işlemi butona tıklanınca başlatılacak

        VBox vbox = new VBox(10, kullaniciListView, silButton);
        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void kullaniciSil() {
        Kullanici seciliKullanici = kullaniciListView.getSelectionModel().getSelectedItem();
        if (seciliKullanici != null) {
            int kullaniciID = seciliKullanici.getId();
            Connection connection = null;
            PreparedStatement statement = null;

            try {
                connection = DatabaseBaglanti.getInstance().getConnection();
                String query = "DELETE FROM kullanicilar WHERE KullaniciID = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, kullaniciID);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    Platform.runLater(() -> {
                        System.out.println("Kullanıcı başarıyla silindi.");
                        kullaniciObservableList.remove(seciliKullanici);
                    });
                } else {
                    System.out.println("Kullanıcı bulunamadı.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Kullanici> listeleKullanicilar() {
        List<Kullanici> kullanicilar = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT k.KullaniciID,k.AdSoyad,k.Email,k.Telefon,d.DaireNumarasi FROM kullanicilar k JOIN daireler d on k.DaireID = d.DaireID";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("KullaniciID");
                String isim = resultSet.getString("AdSoyad");
                String email = resultSet.getString("Email");
                String telefon = resultSet.getString("Telefon");
                int daire = resultSet.getInt("DaireNumarasi");




                Kullanici kullanici = new Kullanici.KullaniciBuilder(id)
                        .setIsim(isim)
                        .setEmail(email)
                        .setTelefon(telefon)
                        .setDaireNo(daire)
                        .build();
                kullanicilar.add(kullanici);
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
        return kullanicilar;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
