import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:addressbook.db";

    public DatabaseHandler() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS contacts ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "name TEXT, "
                   + "phone TEXT, "
                   + "email TEXT, "
                   + "address TEXT)"; // New column for address
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                contacts.add(new Contact(rs.getInt("id"), rs.getString("name"), 
                                         rs.getString("phone"), rs.getString("email"), 
                                         rs.getString("address"))); // Include address
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    public void addContact(Contact contact) {
        String sql = "INSERT INTO contacts(name, phone, email, address) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhone());
            pstmt.setString(3, contact.getEmail());
            pstmt.setString(4, contact.getAddress()); // Add address
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   public void updateContact(Contact contact) {
    String sql = "UPDATE contacts SET name = ?, phone = ?, email = ?, address = ? WHERE id = ?";
    try (Connection conn = DriverManager.getConnection(URL);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        conn.setAutoCommit(false);

        pstmt.setString(1, contact.getName());
        pstmt.setString(2, contact.getPhone());
        pstmt.setString(3, contact.getEmail());
        pstmt.setString(4, contact.getAddress());
        pstmt.setInt(5, contact.getId());
        pstmt.executeUpdate();

        conn.commit();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public void deleteContact(int id) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Metode untuk mencari kontak berdasarkan nama dari database
    public Contact getContactByName(String name) {
        String sql = "SELECT * FROM contacts WHERE name = ?";
        try (Connection conn = DriverManager.getConnection(URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Mengambil data dari result set
                int id = rs.getInt("id");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");
                return new Contact(id, name, phone, email, address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Jika tidak ditemukan
    }
}