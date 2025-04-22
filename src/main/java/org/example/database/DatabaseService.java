package org.example.database;

/*package database;*/

/*import animals.Animal;*/

import org.example.animals.Animal;
import org.example.animals.AnimalFactory;

import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseService {
    private static Connection connection;

    // Инициализация подключения
    public static void connect() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("src/main/resources/config.properties"));

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Подключение к базе установлено.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подключения к базе данных", e);
        }
    }

    // Сохранение животного
    public static void saveAnimal(Animal animal) {
        String sql = "INSERT INTO animals (name, type, birth_date, commands) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, animal.getName());
            stmt.setString(2, animal.getType());
            stmt.setDate(3, Date.valueOf(animal.getBirthDate()));
            stmt.setString(4, String.join(",", animal.getCommands()));

            stmt.executeUpdate();
            System.out.println("Животное сохранено в базу.");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении животного", e);
        }
    }

    // Загрузка всех животных
    public static List<Animal> loadAnimals() {
        List<Animal> list = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM animals");

            while (rs.next()) {
                String name = rs.getString("name");
                String type = rs.getString("type");
                LocalDate birth = rs.getDate("birth_date").toLocalDate();
                String[] cmds = rs.getString("commands").split(",");

                Animal animal = AnimalFactory.create(type, name, birth, cmds);
                for (String cmd : cmds) {
                    animal.learnCommand(cmd);
                }
                list.add(animal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при загрузке животных", e);
        }

        return list;
    }

    public static void disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException ignored) {}
    }
}