package org.example;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuyuruListesiGorKomutu implements Komut {

    @Override
    public void calistir() {

        List<String> duyurular = getAllAnnouncements();


        Stage stage = new Stage();
        VBox root = new VBox(10);


        ListView<String> duyuruListView = new ListView<>();
        duyuruListView.getItems().addAll(duyurular);

        root.getChildren().add(duyuruListView);


        stage.setTitle("Duyuru Listesi");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }


    private List<String> getAllAnnouncements() {
        List<String> duyurular = new ArrayList<>();
        String query = "SELECT Baslik, Icerik FROM duyurular ORDER BY Tarih DESC";  // Tarihe göre sıralama

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/apartmanyonetim", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String baslik = rs.getString("Baslik");
                String icerik = rs.getString("Icerik");
                duyurular.add("Başlık: " + baslik + "\nİçerik: " + icerik);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }

        return duyurular;
    }
}
