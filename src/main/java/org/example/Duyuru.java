package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Duyuru {
    private String baslik;
    private String icerik;
    private Timestamp tarih;

    public Duyuru(String baslik, String icerik, Timestamp tarih) {
        this.baslik = baslik;
        this.icerik = icerik;
        this.tarih = tarih;
    }

    public String getBaslik() {
        return baslik;
    }

    public String getIcerik() {
        return icerik;
    }

    public Timestamp getTarih() {
        return tarih;
    }
    public static List<Duyuru> listele() {
        List<Duyuru> duyurular = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseBaglanti.getInstance().getConnection();
            String query = "SELECT * FROM duyurular ORDER BY Tarih DESC";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String baslik = resultSet.getString("Baslik");
                String icerik = resultSet.getString("Icerik");
                Timestamp tarih = resultSet.getTimestamp("Tarih");

                duyurular.add(new Duyuru(baslik, icerik, tarih));
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

        return duyurular;
    }
}
