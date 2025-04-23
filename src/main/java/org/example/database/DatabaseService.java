package org.example.database;

import org.example.animals.Animal;
import org.example.animals.AnimalFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*import animals.Animal;*/

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*import animals.Animal;*/

public class DatabaseService {
    private final String url;
    private final String user;
    private final String password;

    private Connection connection;

    public DatabaseService(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        connectOnce();
    }

    private void connectOnce() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Подключение к базе установлено");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка подключения к базе: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Соединение закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public void saveAnimal(Animal animal) {
        /*String sql = "INSERT INTO all_animals (name, commands, birthday,source_table ) VALUES (?, ?, ?, ?)";*/
        String tableName = animal.getClass().getSimpleName().toLowerCase(); // например: "dog", "cat", и т.д.
        String sql = "INSERT INTO " + tableName + " (name, commands, birthday) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, animal.getName());
            stmt.setString(2, String.join(",", animal.getCommands()));
            stmt.setDate(3, Date.valueOf(animal.getBirthday()));
            /*stmt.setString(4, animal.getClass().getSimpleName());*/
            stmt.executeUpdate();
            System.out.println("✅ Животное сохранено в базу");
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    public List<Animal> loadAnimals() {
        List<Animal> result = new ArrayList<>();
        String sql = "SELECT * FROM all_animals";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                String name = rs.getString("name");
                LocalDate birth = rs.getDate("birthday").toLocalDate();
                List<String> commands = Arrays.asList(rs.getString("commands").split(","));
                String type = rs.getString("source_table");
                Animal animal = AnimalFactory.create(type, name, birth, commands);

                result.add(animal);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке: " + e.getMessage());
        }

        return result;
    }
}
