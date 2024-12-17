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
import java.util.ArrayList;
import java.util.List;

public class DaireGuncelleKomutu implements Komut {
    private Yonetici yonetici;

    @FXML
    private ListView<Daire> daireListView;

    @FXML
    private Button updateStatusButton;

    public DaireGuncelleKomutu(Yonetici yonetici) {
        this.yonetici = yonetici;
    }

    @FXML
    public void initialize() {
        loadDaires();

        updateStatusButton.setOnAction(event -> {
            Daire selectedDaire = daireListView.getSelectionModel().getSelectedItem();

            if (selectedDaire == null) {
                showAlert("Hata", "Lütfen güncellemek için bir daire seçin.", Alert.AlertType.ERROR);
                return;
            }

            // Durumu toggle etme işlemi
            if (selectedDaire.getDurum() instanceof DaireDurumu.DoluDaire) {
                selectedDaire.setDurum(new DaireDurumu.BosDaire());  // Eğer daire doluysa, boş yapıyoruz
            } else {
                selectedDaire.setDurum(new DaireDurumu.DoluDaire());  // Eğer daire boşsa, dolu yapıyoruz
            }

            // Veritabanında güncelleme yapıyoruz
            guncelleDaireDurumu(selectedDaire);

            // Listeyi yenile
            loadDaires();

            // Başarı mesajı
            showAlert("Başarılı", "Daire durumu başarıyla güncellendi.", Alert.AlertType.INFORMATION);
        });
    }

    private void loadDaires() {
        List<Daire> daireler = listeleDaireler();
        daireListView.getItems().clear();
        daireListView.getItems().addAll(daireler);

        daireListView.setCellFactory(param -> new ListCell<Daire>() {
            @Override
            protected void updateItem(Daire daire, boolean empty) {
                super.updateItem(daire, empty);
                if (empty || daire == null) {
                    setText(null);
                } else {
                    String cellText = "DaireID: " + daire.getDaireID() + "\n" +
                            "Daire Numarası: " + daire.getDaireNumarasi() + "\n" +
                            "Kat: " + daire.getKatNumarasi() + "\n" +
                            "Durum: " + daire.getDurum().durumBilgisi();
                    setText(cellText);
                }
            }
        });
    }

    private List<Daire> listeleDaireler() {
        // Veritabanından daireleri çekme
        List<Daire> daireler = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT DaireID, DaireNumarasi, KatNumarasi, Durum FROM daireler";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int daireID = resultSet.getInt("DaireID");
                int daireNumarasi = resultSet.getInt("DaireNumarasi");
                int katNumarasi = resultSet.getInt("KatNumarasi");
                String durum = resultSet.getString("Durum");

                DaireDurumu daireDurumu = durum.equals("Boş") ? new DaireDurumu.BosDaire() : new DaireDurumu.DoluDaire();

                Daire daire = new Daire(daireID, daireNumarasi, katNumarasi, daireDurumu);
                daireler.add(daire);
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
        return daireler;
    }

    private void guncelleDaireDurumu(Daire daire) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "UPDATE daireler SET Durum = ? WHERE DaireID = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, daire.getDurum().durumBilgisi());
            statement.setInt(2, daire.getDaireID());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Daire durumu başarıyla güncellendi.");
            } else {
                System.out.println("Daire bulunamadı.");
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void calistir() {
        showDaireGuncelleWindow();
    }

    public void showDaireGuncelleWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DaireDuzenle.fxml"));
            loader.setControllerFactory(controllerClass -> new DaireGuncelleKomutu(yonetici));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Daire Durumu Güncelle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Hata", "Daire güncelleme penceresi yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }
}
