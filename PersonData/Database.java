// ============================================================================
// Database.java
// Class penghubung aplikasi dengan database MySQL (db_people) lewat JDBC.
// Isi class mengikuti kode pada Modul 5.
// ============================================================================
import java.sql.*;                              // import seluruh kelas JDBC: Connection, DriverManager, Statement, ResultSet, SQLException

public class Database {                         // deklarasi class Database yang mengurus koneksi dan query
    private Connection connection;              // atribut penyimpan koneksi ke database MySQL
    private Statement statement;                // atribut penyimpan statement untuk mengeksekusi query

    // constructor
    public Database() {                         // konstruktor tanpa parameter; koneksi langsung dibuka saat objek dibuat
        try {                                   // mulai blok try untuk menangkap error koneksi
            connection = DriverManager.getConnection(   // buka koneksi ke MySQL lewat JDBC
                    "jdbc:mysql://localhost:3306/db_people",    // url: server localhost, port 3306, database db_people
                    "root",                     // username MySQL (default Laragon/XAMPP)
                    "");                        // password MySQL (default kosong)
            statement = connection.createStatement();   // buat statement dari koneksi yang sudah terbuka
            System.out.println("Koneksi ke database berhasil!");    // cetak ke console sebagai bukti koneksi berhasil
        } catch (SQLException e) {              // menangkap error SQL (driver tidak ada, MySQL mati, DB tidak ditemukan, dll)
            throw new RuntimeException(e);      // lempar ulang error agar program tidak lanjut tanpa koneksi
        }                                       // akhir blok catch
    }                                           // akhir konstruktor

    // digunakan untuk SELECT
    public ResultSet selectQuery(String sql) {  // parameter sql berisi perintah SELECT
        try {                                   // mulai blok try untuk menangkap error query
            statement.executeQuery(sql);        // jalankan query SELECT ke database
            return statement.getResultSet();    // kembalikan hasil query agar bisa dibaca pemanggil
        } catch (SQLException e) {              // menangkap error saat query SELECT dijalankan
            throw new RuntimeException(e);      // lempar ulang error agar ketahuan penyebabnya
        }                                       // akhir blok catch
    }                                           // akhir method selectQuery

    // digunakan untuk UPDATE, INSERT, DELETE
    public int insertUpdateDeleteQuery(String sql) {    // parameter sql berisi perintah INSERT/UPDATE/DELETE
        try {                                           // mulai blok try untuk menangkap error query
            return statement.executeUpdate(sql);        // jalankan query dan kembalikan jumlah baris yang terpengaruh
        } catch (SQLException e) {                      // menangkap error saat query dijalankan
            throw new RuntimeException(e);              // lempar ulang error agar ketahuan penyebabnya
        }                                               // akhir blok catch
    }                                                   // akhir method insertUpdateDeleteQuery

    // getter
    public Statement getStatement() {           // getter untuk atribut statement
        return statement;                       // kembalikan statement milik class ini
    }                                           // akhir method getStatement
}                                               // akhir class Database