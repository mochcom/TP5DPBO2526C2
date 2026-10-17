// Class model (blueprint) untuk satu data orang di Rumah Sakit Psikologi.
// Class ini tidak punya tampilan GUI, hanya menyimpan data dan menyediakan akses ke data itu.
public class Person {

    // ===== ATRIBUT (private = hanya bisa diakses lewat getter/setter) =====
    private String id;          // ID unik orang, contoh: "P001"
    private String nama;        // Nama lengkap, contoh: "Rina Amelia"
    private int tahunLahir;     // Tahun lahir berupa angka, contoh: 1998
    private String kategori;    // Peran di RS Psikologi: Pasien, Psikolog, dst.
    private String mbti;        // Tipe kepribadian MBTI, contoh: "INFJ"

    // ===== KONSTRUKTOR =====
    // Dipanggil saat membuat objek baru: new Person("P001", "Rina", 1998, "Pasien", "INFJ")
    public Person(String id, String nama, int tahunLahir, String kategori, String mbti) {
        this.id = id;                   // isi atribut id dengan parameter id
        this.nama = nama;               // isi atribut nama dengan parameter nama
        this.tahunLahir = tahunLahir;   // isi atribut tahunLahir dengan parameter tahunLahir
        this.kategori = kategori;       // isi atribut kategori dengan parameter kategori
        this.mbti = mbti;               // isi atribut mbti dengan parameter mbti
    }

    // ===== GETTER (untuk MEMBACA nilai atribut) =====
    public String getId() {
        return id;                      // mengembalikan nilai id
    }

    public String getNama() {
        return nama;                    // mengembalikan nilai nama
    }

    public int getTahunLahir() {
        return tahunLahir;              // mengembalikan nilai tahunLahir
    }

    public String getKategori() {
        return kategori;                // mengembalikan nilai kategori
    }

    public String getMbti() {
        return mbti;                    // mengembalikan nilai mbti
    }

    // ===== SETTER (untuk MENGUBAH nilai atribut, dipakai saat Update data) =====
    public void setId(String id) {
        this.id = id;                   // mengganti nilai id
    }

    public void setNama(String nama) {
        this.nama = nama;               // mengganti nilai nama
    }

    public void setTahunLahir(int tahunLahir) {
        this.tahunLahir = tahunLahir;   // mengganti nilai tahunLahir
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;       // mengganti nilai kategori
    }

    public void setMbti(String mbti) {
        this.mbti = mbti;               // mengganti nilai mbti
    }
}