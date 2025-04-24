package org.example.database;

import org.example.animals.*;

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
        String sql1 = "INSERT INTO " + tableName + " (name, commands, birthday) VALUES (?, ?, ?)";
        String sql2 = "INSERT INTO all_animals (name, birthday, commands, source_table) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt1 = connection.prepareStatement(sql1);
             PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
                stmt1.setString(1, animal.getName());
                stmt1.setString(2, String.join(",", animal.getCommands()));
                stmt1.setDate(3, Date.valueOf(animal.getBirthday()));

                stmt2.setString(1, animal.getName());
                stmt2.setDate(2, Date.valueOf(animal.getBirthday()));
                stmt2.setString(3, String.join(",", animal.getCommands()));
                stmt2.setString(4, tableName);
                /*stmt2.setString(4, animal.getClass().getSimpleName());*/

                stmt1.executeUpdate();
                stmt2.executeUpdate();

                System.out.println("✅ Животное сохранено в базу");
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    /*public List<Animal> loadAnimals() {
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
    }*/

    public List<Animal> loadAnimals() {
        List<Animal> animals = new ArrayList<>();
        String sql = "SELECT name, birthday, commands, source_table FROM all_animals"; // Измените на название вашей таблицы и столбцов

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String commands = rs.getString("commands");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String type = rs.getString("source_table");

                Animal animal = switch (type) {
                    case "Dogs" -> new Dogs(name, birthday, parseCommands(commands));
                    case "Cats" -> new Cats(name, birthday, parseCommands(commands));
                    case "Hamsters" -> new Hamsters(name, birthday, parseCommands(commands));
                    case "Horses" -> new Horses(name, birthday, parseCommands(commands));
                    case "Donkeys" -> new Donkeys(name, birthday, parseCommands(commands));
                    default -> {
                        System.out.println("Неизвестный тип животного: " + type);
                        yield null;
                    }
                };

                if (animal != null) {
                    animals.add(animal);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке животных: " + e.getMessage());
        }

        return animals;
    }

    private List<String> parseCommands(String commands) {
        return commands.isEmpty() ? new ArrayList<>() : Arrays.asList(commands.split(","));
    }

    public void showCommandsByName(String name) {
        String sql = "SELECT commands, source_table FROM all_animals WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String commandsStr = rs.getString("commands");
                String type = rs.getString("source_table");
                List<String> commands = Arrays.asList(commandsStr.split(","));
                System.out.println("Команды животного '" + name + "' (" + type + "): " + commands);
            } else {
                System.out.println("Животное с именем '" + name + "' не найдено.");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при поиске команд: " + e.getMessage());
        }
    }

    /*public void showCommandsByName(String name) {
        String sql = "SELECT commands, source_table FROM all_animals WHERE name = ?";*/


        public void trainAnimalCommand(String name, String newCommand) {
            String selectSql = "SELECT commands, source_table FROM all_animals WHERE name = ?";
            String updateSql = "UPDATE ? SET commands = ? WHERE name = ?";

            try (
                    PreparedStatement selectStmt = connection.prepareStatement(selectSql);
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql)
            ) {
                selectStmt.setString(1, name);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String existingCommands = rs.getString("commands");
                    String type = rs.getString("source_table");
                    List<String> commandList = new ArrayList<>();

                    if (existingCommands != null && !existingCommands.isBlank()) {
                        commandList = new ArrayList<>(Arrays.asList(existingCommands.split(",")));
                    }

                    if (!commandList.contains(newCommand)) {
                        commandList.add(newCommand);
                    }

                    String updatedCommands = String.join(",", commandList);
                    updateStmt.setString(1, type);
                    updateStmt.setString(2, updatedCommands);
                    updateStmt.setString(3, name);
                    int rows = updateStmt.executeUpdate();

                    if (rows > 0) {
                        System.out.println("✅ Команда добавлена животному '" + name + "'");
                    } else {
                        System.out.println("⚠ Не удалось обновить команды.");
                    }

                } else {
                    System.out.println("❌ Животное с именем '" + name + "' не найдено в базе.");
                }

            } catch (SQLException e) {
                System.err.println("Ошибка при обучении: " + e.getMessage());
            }
        }

}
