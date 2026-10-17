// ============================================================================
// PeopleMenu.java
// Class utama aplikasi PeopleData (tema: Rumah Sakit Psikologi).
// Class ini terhubung dengan PeopleMenu.form (tampilan) lewat "field name".
// Fitur: Create, Read, Update, Delete + konfirmasi hapus + validasi input.
// [TP5] Data kini disimpan di database MySQL (db_people), bukan lagi ArrayList.
// Baris yang diberi tanda [TP5] adalah baris baru/yang dimodifikasi dari TP 4.
// ============================================================================

// ---------------------------- IMPORT ----------------------------------------
import javax.swing.*;                           // seluruh komponen Swing: JFrame, JPanel, JTable, JButton, JOptionPane, dst.
import javax.swing.table.DefaultTableModel;     // model data JTable, memungkinkan kita menambah baris dan mengganti isi tabel
import java.awt.event.MouseAdapter;             // class kerangka listener mouse, cukup menimpa method yang dibutuhkan
import java.awt.event.MouseEvent;               // objek yang membawa informasi kejadian mouse (klik, posisi, dll.)
import java.sql.ResultSet;                      // [TP5] untuk menampung dan membaca hasil query SELECT dari database
import java.sql.SQLException;                   // [TP5] untuk menangani error saat membaca data dari ResultSet
import java.time.Year;                          // untuk mengambil tahun sekarang sebagai batas atas Tahun Lahir
// [TP5] import java.util.ArrayList dihapus karena penyimpanan data tidak lagi memakai ArrayList

public class PeopleMenu {                       // deklarasi class; namanya harus sama dengan nama file dan nama form

    // ========================================================================
    // ATRIBUT KOMPONEN FORM
    // Nama variabel HARUS sama persis dengan "field name" di PeopleMenu.form.
    // Kalau berbeda, muncul error "Cannot bind: field does not exist".
    // ========================================================================
    private JPanel mainPanel;                   // panel utama (root), dipasang ke JFrame di method main()
    private JPanel formPanel;                   // panel penampung label dan isian (wajib ada karena field name-nya terisi)
    private JTextField idField;                 // kotak isian ID
    private JTextField namaField;               // kotak isian Nama
    private JTextField tahunLahirField;         // kotak isian Tahun Lahir (teks yang nanti diubah jadi angka)
    private JComboBox<String> kategoriComboBox; // dropdown Kategori; <String> artinya itemnya berupa teks
    private JComboBox<String> mbtiComboBox;     // dropdown MBTI (atribut baru, komponen bukan JTextField untuk bonus +20)
    private JPanel buttonPanel;                 // panel penampung tiga tombol
    private JButton addUpdateButton;            // tombol dua fungsi: "Add" (tambah) atau "Update" (ubah)
    private JButton deleteButton;               // tombol Delete, hanya tampil saat ada baris terpilih
    private JButton cancelButton;               // tombol Cancel untuk mengosongkan form
    private JScrollPane peopleScrollPane;       // pembungkus tabel agar bisa di-scroll
    private JTable peopleTable;                 // tabel penampil seluruh data Person

    // ========================================================================
    // ATRIBUT TAMBAHAN (bukan dari form)
    // ========================================================================
    private Database database;                 // [TP5] objek Database untuk berhubungan dengan MySQL db_people
    // [TP5] atribut personList (ArrayList<Person>) dihapus, data kini langsung diambil dari database
    private int selectedIndex = -1;             // indeks baris yang dipilih; -1 berarti belum ada yang dipilih (mode Add)
    private final String PILIH_KATEGORI = "-- Pilih Kategori --";   // [TP5] teks placeholder item pertama dropdown Kategori (tanda belum memilih)
    private final String PILIH_MBTI = "-- Pilih MBTI --";           // [TP5] teks placeholder item pertama dropdown MBTI (tanda belum memilih)

    // ========================================================================
    // KONSTRUKTOR
    // Dijalankan sekali saat objek PeopleMenu dibuat di main().
    // Tugasnya menyiapkan data, mengisi tabel, mengisi dropdown, dan memasang listener.
    // ========================================================================
    public PeopleMenu() {
        database = new Database();             // [TP5] membuat objek Database (koneksi ke db_people dibuka di dalam konstruktor Database, sesuai modul)

        // [TP5] baris "personList = new ArrayList<>()" dan "populateList()" dihapus, data awal kini ada di database
        peopleTable.setModel(setTable());       // [TP5] setTable() mengembalikan model berisi data database, lalu dipasang ke JTable (sesuai modul)

        // mengisi pilihan dropdown Kategori lewat kode; setModel menimpa isi bawaan dari form
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                PILIH_KATEGORI,                                 // [TP5] item pertama berupa placeholder agar dropdown bisa "kosong"
                "Pasien", "Psikolog", "Psikiater", "Perawat", "Konselor"
        }));

        // mengisi pilihan dropdown MBTI dengan 16 tipe kepribadian
        mbtiComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                PILIH_MBTI,                                     // [TP5] item pertama berupa placeholder agar dropdown bisa "kosong"
                "INTJ", "INTP", "ENTJ", "ENTP", "INFJ", "INFP", "ENFJ", "ENFP",
                "ISTJ", "ISFJ", "ESTJ", "ESFJ", "ISTP", "ISFP", "ESTP", "ESFP"
        }));

        deleteButton.setVisible(false);         // sembunyikan tombol Delete karena belum ada data yang dipilih

        // ---------------- LISTENER tombol Add/Update ----------------
        // "e -> {...}" adalah lambda: kode di dalamnya jalan setiap tombol diklik
        addUpdateButton.addActionListener(e -> {
            if (selectedIndex == -1) {          // belum ada baris terpilih -> user ingin menambah data
                insertData();                   // jalankan proses Create
            } else {                            // ada baris terpilih -> user ingin mengubah data
                updateData();                   // jalankan proses Update
            }
        });

        // ---------------- LISTENER tombol Delete ----------------
        deleteButton.addActionListener(e -> deleteData());  // klik Delete -> jalankan proses hapus (dengan konfirmasi)

        // ---------------- LISTENER tombol Cancel ----------------
        cancelButton.addActionListener(e -> clearForm());   // klik Cancel -> form dikembalikan ke kondisi awal

        // ---------------- LISTENER klik baris tabel ----------------
        // MouseAdapter dibuat sebagai anonymous class, hanya method mouseClicked yang kita isi
        peopleTable.addMouseListener(new MouseAdapter() {
            @Override                                           // menandakan kita menimpa method bawaan MouseAdapter
            public void mousePressed(MouseEvent e) {            // [TP5] dipanggil otomatis saat baris tabel ditekan (sesuai modul memakai mousePressed)
                int row = peopleTable.getSelectedRow();         // indeks baris yang diklik (mulai dari 0), -1 jika tidak ada
                if (row == -1) {                                // jika klik tidak mengenai baris data
                    return;                                     // hentikan method, tidak ada yang dikerjakan
                }

                selectedIndex = row;                            // simpan indeks baris terpilih untuk Update/Delete

                // [TP5] kolom tabel: 0 = No, 1 = ID, 2 = Nama, 3 = Tahun Lahir, 4 = Kategori, 5 = MBTI
                idField.setText(peopleTable.getModel().getValueAt(selectedIndex, 1).toString());           // [TP5] ambil ID langsung dari sel tabel, tampilkan di form
                namaField.setText(peopleTable.getModel().getValueAt(selectedIndex, 2).toString());         // [TP5] ambil Nama dari sel tabel, tampilkan di form
                tahunLahirField.setText(peopleTable.getModel().getValueAt(selectedIndex, 3).toString());   // [TP5] ambil Tahun Lahir dari sel tabel (Object -> String)
                kategoriComboBox.setSelectedItem(peopleTable.getModel().getValueAt(selectedIndex, 4).toString());  // [TP5] pilih item dropdown Kategori sesuai isi sel tabel
                mbtiComboBox.setSelectedItem(peopleTable.getModel().getValueAt(selectedIndex, 5).toString());      // [TP5] pilih item dropdown MBTI sesuai isi sel tabel

                addUpdateButton.setText("Update");              // ubah teks tombol menjadi mode edit
                deleteButton.setVisible(true);                  // munculkan tombol Delete
            }
        });
    }

    // [TP5] method populateList() dihapus seluruhnya.
    // Data awal (P001 - P006) kini dimasukkan lewat skrip SQL (db_people.sql).

    // ========================================================================
    // setTable(): menggambar ulang tabel berdasarkan isi tabel person di database (READ)
    // Dipanggil setiap kali data berubah supaya tampilan selalu sinkron.
    // ========================================================================
    public final DefaultTableModel setTable() {     // [TP5] method kini mengembalikan DefaultTableModel (sesuai modul)
        // judul kolom; urutannya harus sama dengan urutan isi row di bawah
        String[] columnNames = {"No", "ID", "Nama", "Tahun Lahir", "Kategori", "MBTI"};

        // model tabel baru dengan judul kolom di atas dan 0 baris awal
        DefaultTableModel tmp = new DefaultTableModel(columnNames, 0);      // [TP5] nama variabel mengikuti modul (tmp)

        try {                                                               // [TP5] blok try karena query dan ResultSet bisa melempar SQLException
            ResultSet resultSet = database.selectQuery("SELECT * FROM person ORDER BY id");     // [TP5] ambil semua data dari tabel person di database

            int i = 0;                                  // [TP5] penghitung baris, dipakai untuk nomor urut tampilan

            while (resultSet.next()) {                  // [TP5] pindah ke baris hasil berikutnya; false jika sudah habis
                Object[] row = new Object[6];           // [TP5] satu baris tabel berisi 6 kolom
                row[0] = i + 1;                         // [TP5] kolom No (mulai dari 1)
                row[1] = resultSet.getString("id");             // [TP5] kolom ID dari kolom id di database
                row[2] = resultSet.getString("nama");           // [TP5] kolom Nama dari kolom nama di database
                row[3] = resultSet.getString("thn_lahir");      // [TP5] kolom Tahun Lahir dari kolom thn_lahir di database
                row[4] = resultSet.getString("kategori");       // [TP5] kolom Kategori dari kolom kategori di database
                row[5] = resultSet.getString("mbti");           // [TP5] kolom MBTI dari kolom mbti di database
                tmp.addRow(row);                        // [TP5] masukkan baris ke model
                i++;                                    // [TP5] naikkan penghitung untuk baris berikutnya
            }
        } catch (SQLException e) {                      // [TP5] dijalankan jika terjadi error saat membaca data
            throw new RuntimeException(e);              // [TP5] lempar ulang error agar terlihat di console (sesuai modul)
        }

        return tmp;                                     // [TP5] kembalikan model agar bisa dipasang ke peopleTable
    }

    // ========================================================================
    // showError(): helper untuk menampilkan dialog error
    // Dibuat agar pemanggilan dialog yang sama tidak ditulis berulang.
    // ========================================================================
    private void showError(String pesan) {
        // parameter: parent (null = tengah layar), isi pesan, judul dialog, tipe ikon (silang merah)
        JOptionPane.showMessageDialog(null, pesan, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ========================================================================
    // escape(): [TP5] helper agar teks aman dimasukkan ke dalam query SQL
    // Tanda petik tunggal (') dan backslash (\) pada teks bisa merusak query,
    // contoh nama "D'Angelo", maka karakter tersebut harus di-escape.
    // ========================================================================
    private String escape(String teks) {               // [TP5] menerima teks mentah dari form
        return teks.replace("\\", "\\\\").replace("'", "''");   // [TP5] backslash digandakan, petik tunggal diubah jadi dua petik tunggal
    }

    // ========================================================================
    // readAndValidate(): membaca isi form lalu memvalidasinya
    // ignoreId : [TP5] ID yang dikecualikan saat cek ID duplikat
    //            ("" saat Add, ID lama dari baris terpilih saat Update)
    // return   : objek Person valid, atau null jika ada input yang salah
    // ========================================================================
    private Person readAndValidate(String ignoreId) {         // [TP5] parameter diganti dari int ignoreIndex menjadi String ignoreId
        String id = idField.getText().trim();                   // ambil ID; trim() membuang spasi di awal/akhir
        String nama = namaField.getText().trim();               // ambil Nama; trim() membuang spasi di awal/akhir
        String tahunText = tahunLahirField.getText().trim();    // ambil Tahun Lahir, masih berupa teks

        // ---------- VALIDASI 1: input tidak boleh kosong ----------
        if (id.isEmpty()) {                                     // isEmpty() true jika teks tidak berisi karakter
            showError("ID tidak boleh kosong!");                // tampilkan dialog error
            return null;                                        // null = validasi gagal, hentikan method
        }
        if (nama.isEmpty()) {                                   // cek Nama
            showError("Nama tidak boleh kosong!");
            return null;
        }
        if (tahunText.isEmpty()) {                              // cek Tahun Lahir
            showError("Tahun Lahir tidak boleh kosong!");
            return null;
        }
        if (kategoriComboBox.getSelectedItem().toString().equals(PILIH_KATEGORI)) {     // [TP5] true jika Kategori masih berupa placeholder (belum dipilih)
            showError("Kategori belum dipilih!");               // [TP5] tampilkan dialog error
            return null;                                        // [TP5] validasi gagal, hentikan method
        }
        if (mbtiComboBox.getSelectedItem().toString().equals(PILIH_MBTI)) {             // [TP5] true jika MBTI masih berupa placeholder (belum dipilih)
            showError("MBTI belum dipilih!");                   // [TP5] tampilkan dialog error
            return null;                                        // [TP5] validasi gagal, hentikan method
        }

        // ---------- VALIDASI 2: ID tidak boleh duplikat ----------
        if (isIdExists(id, ignoreId)) {                         // [TP5] cek ke database; true jika ID sudah dipakai data lain
            showError("ID \"" + id + "\" sudah digunakan! Gunakan ID yang berbeda.");
            return null;
        }

        // ---------- VALIDASI 3: Tahun Lahir harus angka ----------
        int tahunLahir;                                         // dideklarasikan di luar try agar bisa dipakai setelah try-catch
        try {
            tahunLahir = Integer.parseInt(tahunText);           // ubah teks jadi angka; gagal -> NumberFormatException
        } catch (NumberFormatException ex) {                    // dijalankan hanya jika teks bukan angka (mis. "abc", "19.9")
            showError("Tahun Lahir harus berupa angka! Contoh: 1998");
            return null;
        }

        // ---------- VALIDASI 4: Tahun Lahir harus masuk akal ----------
        int tahunSekarang = Year.now().getValue();              // tahun saat program dijalankan
        if (tahunLahir < 1900 || tahunLahir > tahunSekarang) {  // "||" artinya ATAU: salah satu terpenuhi sudah cukup
            showError("Tahun Lahir harus di antara 1900 dan " + tahunSekarang + "!");
            return null;
        }

        // ---------- SEMUA VALID: bentuk objek Person ----------
        String kategori = kategoriComboBox.getSelectedItem().toString();    // item dropdown terpilih (Object -> String)
        String mbti = mbtiComboBox.getSelectedItem().toString();            // dropdown tidak perlu divalidasi, user hanya bisa memilih
        return new Person(id, nama, tahunLahir, kategori, mbti);            // kembalikan objek valid
    }

    // ========================================================================
    // insertData(): CREATE, menambah data baru ke database
    // ========================================================================
    public void insertData() {
        Person baru = readAndValidate("");                      // [TP5] "" = tidak ada ID yang dikecualikan pada cek duplikat
        if (baru == null) {                                     // null berarti validasi gagal (dialog error sudah tampil)
            return;                                             // batalkan penambahan
        }

        // [TP5] menyusun query INSERT; teks dibungkus petik tunggal, angka (thn_lahir) tidak
        String sql = "INSERT INTO person (id, nama, thn_lahir, kategori, mbti) VALUES ('"
                + escape(baru.getId()) + "', '"                 // [TP5] nilai kolom id
                + escape(baru.getNama()) + "', "                // [TP5] nilai kolom nama
                + baru.getTahunLahir() + ", '"                  // [TP5] nilai kolom thn_lahir (angka, tanpa petik)
                + escape(baru.getKategori()) + "', '"           // [TP5] nilai kolom kategori
                + escape(baru.getMbti()) + "')";                // [TP5] nilai kolom mbti
        database.insertUpdateDeleteQuery(sql);                  // [TP5] jalankan query INSERT ke database

        peopleTable.setModel(setTable());                       // [TP5] refresh tabel dengan data terbaru dari database
        clearForm();                                            // kosongkan form untuk input berikutnya
        JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);     // pesan sukses dengan ikon informasi
    }

    // ========================================================================
    // updateData(): UPDATE, mengubah data yang sedang dipilih di database
    // ========================================================================
    public void updateData() {
        String idLama = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();   // [TP5] ID asli baris terpilih, dipakai sebagai kunci WHERE
        Person hasil = readAndValidate(idLama);                 // [TP5] ID lama dikecualikan dari cek duplikat ID
        if (hasil == null) {                                    // validasi gagal
            return;                                             // batalkan perubahan
        }

        // [TP5] menyusun query UPDATE; WHERE memakai ID lama agar hanya baris terpilih yang berubah
        String sql = "UPDATE person SET id = '" + escape(hasil.getId())            // [TP5] set kolom id (boleh berubah)
                + "', nama = '" + escape(hasil.getNama())                           // [TP5] set kolom nama
                + "', thn_lahir = " + hasil.getTahunLahir()                       // [TP5] set kolom thn_lahir (angka, tanpa petik)
                + ", kategori = '" + escape(hasil.getKategori())                    // [TP5] set kolom kategori
                + "', mbti = '" + escape(hasil.getMbti())                           // [TP5] set kolom mbti
                + "' WHERE id = '" + escape(idLama) + "'";                          // [TP5] syarat: hanya baris dengan ID lama
        database.insertUpdateDeleteQuery(sql);                  // [TP5] jalankan query UPDATE ke database

        peopleTable.setModel(setTable());                       // [TP5] refresh tabel dengan data terbaru dari database
        clearForm();                                            // kembali ke mode Add
        JOptionPane.showMessageDialog(null, "Data berhasil diubah!",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    // ========================================================================
    // deleteData(): DELETE, menghapus data dari database setelah user mengonfirmasi
    // ========================================================================
    public void deleteData() {
        if (selectedIndex == -1) {                              // jaga-jaga jika belum ada baris terpilih
            showError("Pilih data yang ingin dihapus terlebih dahulu!");
            return;
        }

        String id = peopleTable.getModel().getValueAt(selectedIndex, 1).toString();       // [TP5] ambil ID baris terpilih langsung dari tabel
        String nama = peopleTable.getModel().getValueAt(selectedIndex, 2).toString();     // [TP5] ambil Nama baris terpilih untuk ditampilkan di pesan

        // dialog konfirmasi; mengembalikan int yang menunjukkan tombol yang ditekan user
        int pilihan = JOptionPane.showConfirmDialog(
                null,                                           // parent: dialog muncul di tengah layar
                "Yakin ingin menghapus data \"" + nama + "\" (ID: " + id + ")?",  // [TP5] isi pertanyaan memakai nama dan id dari tabel
                "Konfirmasi Hapus",                             // judul dialog
                JOptionPane.YES_NO_OPTION,                      // tombol yang tampil: Yes dan No
                JOptionPane.WARNING_MESSAGE);                   // ikon peringatan

        if (pilihan == JOptionPane.YES_OPTION) {                // hanya hapus jika user menekan Yes
            String sql = "DELETE FROM person WHERE id = '" + escape(id) + "'";    // [TP5] query DELETE untuk baris dengan ID terpilih
            database.insertUpdateDeleteQuery(sql);              // [TP5] jalankan query DELETE ke database
            peopleTable.setModel(setTable());                   // [TP5] refresh tabel dengan data terbaru dari database
            clearForm();                                        // reset form dan kembali ke mode Add
            JOptionPane.showMessageDialog(null, "Data berhasil dihapus!",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
        // jika user menekan No atau menutup dialog (X), tidak ada yang terjadi dan data tetap aman
    }

    // ========================================================================
    // clearForm(): mengembalikan form ke kondisi awal (mode Add)
    // ========================================================================
    public void clearForm() {
        idField.setText("");                                    // kosongkan isian ID
        namaField.setText("");                                  // kosongkan isian Nama
        tahunLahirField.setText("");                            // kosongkan isian Tahun Lahir
        kategoriComboBox.setSelectedIndex(0);                   // dropdown Kategori kembali ke item pertama
        mbtiComboBox.setSelectedIndex(0);                       // dropdown MBTI kembali ke item pertama
        addUpdateButton.setText("Add");                         // tombol kembali bertugas menambah data
        deleteButton.setVisible(false);                         // sembunyikan tombol Delete
        selectedIndex = -1;                                     // tandai tidak ada baris terpilih
        peopleTable.clearSelection();                           // hilangkan sorotan baris di tabel
    }

    // ========================================================================
    // isIdExists(): mengecek ke database apakah ID sudah dipakai data lain
    // id       : ID yang ingin dicek
    // ignoreId : [TP5] ID yang dilewati (agar data yang sedang diedit tidak dianggap duplikat dirinya sendiri)
    // return   : true jika ID sudah ada, false jika masih aman
    // ========================================================================
    private boolean isIdExists(String id, String ignoreId) {   // [TP5] parameter kedua diganti dari int menjadi String
        // [TP5] hitung jumlah baris ber-ID sama, kecuali baris dengan ID yang dikecualikan
        String sql = "SELECT COUNT(*) FROM person WHERE id = '" + escape(id.trim())
                + "' AND id <> '" + escape(ignoreId) + "'";     // [TP5] "<>" artinya tidak sama dengan
        try {                                                   // [TP5] blok try karena membaca ResultSet bisa melempar SQLException
            ResultSet rs = database.selectQuery(sql);           // [TP5] jalankan query COUNT ke database
            rs.next();                                          // [TP5] pindah ke baris hasil pertama (COUNT selalu menghasilkan 1 baris)
            return rs.getInt(1) > 0;                            // [TP5] true jika jumlahnya lebih dari 0 (ID sudah terpakai)
        } catch (SQLException e) {                              // [TP5] dijalankan jika terjadi error saat membaca hasil
            showError("Gagal memeriksa ID di database!\n" + e.getMessage());   // [TP5] tampilkan dialog error beserta penyebabnya
            return true;                                        // [TP5] anggap ID sudah ada agar data tidak tersimpan saat pengecekan gagal
        }
    }

    // ========================================================================
    // main(): titik awal eksekusi program
    // Agar window muncul, kita bungkus mainPanel ke dalam JFrame.
    // ========================================================================
    public static void main(String[] args) {
        JFrame frame = new JFrame("People Data - RS Psikologi");    // membuat window dengan judul

        PeopleMenu menu = null;                                     // [TP5] disiapkan null dulu agar bisa dibuat di dalam try
        try {                                                       // [TP5] blok try untuk menangkap kegagalan koneksi database
            menu = new PeopleMenu();                                // membuat objek menu; konstruktor langsung berjalan
        } catch (RuntimeException ex) {                             // [TP5] dijalankan jika koneksi/query database gagal
            ex.printStackTrace();                                   // [TP5] cetak detail error lengkap ke console IntelliJ
            JOptionPane.showMessageDialog(null,                     // [TP5] tampilkan dialog agar penyebab langsung terlihat
                    "Gagal terhubung ke database!\n" + ex.getCause(),   // [TP5] isi pesan: penyebab error dari JDBC
                    "Error Koneksi", JOptionPane.ERROR_MESSAGE);    // [TP5] judul dialog dan ikon error
            System.exit(1);                                         // [TP5] hentikan program karena tanpa database aplikasi tidak bisa jalan
        }

        frame.setContentPane(menu.mainPanel);                       // jadikan mainPanel sebagai isi window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // klik tombol X menghentikan seluruh program
        frame.pack();                                               // ukuran window menyesuaikan isi komponen
        frame.setLocationRelativeTo(null);                          // window muncul di tengah layar
        frame.setVisible(true);                                     // tampilkan window
    }
}