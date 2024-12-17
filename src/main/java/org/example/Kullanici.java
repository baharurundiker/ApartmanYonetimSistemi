package org.example;
import java.sql.*;
public class Kullanici implements Gozlemci {
    private int Id;
    private String isim;
    private String email;
    private String telefon;
    private int daireId;
    private int daireNo;

    protected Kullanici(int builderId) {
        this.Id = builderId;
    }

    public static class KullaniciBuilder {
        private int Id;
        private String isim;
        private String email;
        private String telefon;
        private int daireId;
        private int daireNo;

        public KullaniciBuilder(int Id) {
            this.Id = Id;
        }

        public KullaniciBuilder setIsim(String isim) {
            this.isim = isim;
            return this;
        }

        public KullaniciBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public KullaniciBuilder setTelefon(String telefon) {
            this.telefon = telefon;
            return this;
        }

        public KullaniciBuilder setDaireId(int daireId) {
            this.daireId = daireId;
            return this;
        }

        public KullaniciBuilder setDaireNo(int daireNo) {
            this.daireNo = daireNo;
            return this;
        }

        public Kullanici build() {
            Kullanici kullanici = new Kullanici(this.Id);
            kullanici.isim = this.isim;
            kullanici.email = this.email;
            kullanici.telefon = this.telefon;
            kullanici.daireId = this.daireId;
            kullanici.daireNo = this.daireNo;

            // Fetch data from the database
            kullanici.veritabaniGetir();

            return kullanici;
        }
    }

    public void veritabaniGetir() {
        String query = "SELECT k.AdSoyad, k.Email, k.Telefon, k.DaireID, d.DaireNumarasi FROM kullanicilar k JOIN daireler d ON k.DaireID = d.DaireId " +
                "WHERE k.KullaniciID = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/apartmanyonetim", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, this.Id);  // Kullanıcı ID'sini sorguya bağla
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    this.isim = rs.getString("AdSoyad");
                    this.email = rs.getString("Email");
                    this.telefon = rs.getString("Telefon");
                    this.daireId = rs.getInt("DaireID");
                    this.daireNo = rs.getInt("DaireNumarasi");

                    // Debugging output
                    System.out.println("Kullanıcı Verisi: " + this.isim + ", " + this.email + ", " + this.telefon + ", " + this.daireId + ", " + this.daireNo);
                } else {
                    System.out.println("Kullanıcı bulunamadı.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }
    }

    public int getId() {
        return Id;
    }

    public String getIsim() {
        return isim;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefon() {
        return telefon;
    }

    public int getDaireId() {
        return daireId;
    }

    public int getDaireNo() {
        return daireNo;
    }

    @Override
    public String toString() {
        return "ID: " + Id + ", İsim: " + isim + ", Email: " + email + ", Telefon: " + telefon + ", Daire No: " + daireNo;
    }

    @Override
    public void guncelle(String mesaj) {
        // Implement update logic here
        System.out.println("Kullanıcı güncelleniyor: " + mesaj);
    }
}
