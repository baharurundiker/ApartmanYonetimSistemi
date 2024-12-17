package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseBaglanti {
//singelton
    private static DatabaseBaglanti instance;
    private Connection connection;


    private static final String URL = "jdbc:mysql://localhost:3306/apartmanyonetim";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    private DatabaseBaglanti() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Bağlantı başarılı!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Veritabanı bağlantısı kurulamadı.", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeConnection()));
    }


    public static DatabaseBaglanti getInstance() {
        if (instance == null) {
            synchronized (DatabaseBaglanti.class) {
                if (instance == null) {
                    instance = new DatabaseBaglanti();
                }
            }
        }
        return instance;
    }


    public Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            try {

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Yeni bağlantı kurulamadı.", e);
            }
        }
        return connection;
    }


    private boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Bağlantı kapatıldı.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcı girişi metodu
    public static Kullanici kullaniciGirisi(String email, String password) {
        String query = "SELECT * FROM kullanicilar WHERE Email = ? AND Sifre = ?";

        try (Connection conn = getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("KullaniciID");
                String isim = rs.getString("AdSoyad");
                String emailFromDB = rs.getString("Email");
                String role = rs.getString("Rol");

                if ("Yönetici".equalsIgnoreCase(role)) {
                    return new Yonetici(id);
                } else {
                    return new Sakin(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        DatabaseBaglanti dbConnection = DatabaseBaglanti.getInstance();
        Connection conn = dbConnection.getConnection();


    }
}
