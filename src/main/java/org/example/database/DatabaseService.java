package org.example.database;

import org.example.animals.Animal;
import org.example.animals.AnimalFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*import animals.Animal;*/

public class DatabaseService {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseService(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void saveAnimal(Animal animal) {
        String sql = "INSERT INTO animals (name, type, birth_date, commands) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, animal.getName());
            stmt.setString(2, animal.getClass().getSimpleName());
            stmt.setDate(3, Date.valueOf(animal.getBirthday()));
            stmt.setString(4, String.join(",", animal.getCommands()));

            stmt.executeUpdate();
            System.out.println("✅ Животное сохранено в базу.");
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    public List<Animal> loadAnimals() {
        List<Animal> result = new ArrayList<>();
        String sql = "SELECT * FROM animals";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                String name = rs.getString("name");
                LocalDate birthDate = rs.getDate("birth_date").toLocalDate();
                List<String> commands = Arrays.stream(rs.getString("commands").split(","))
                        .map(String::trim).collect(Collectors.toList());

                // здесь можно использовать фабрику
                Animal animal = AnimalFactory.create(type, name, birthDate, commands);
                result.add(animal);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке: " + e.getMessage());
        }

        return result;
    }
}
