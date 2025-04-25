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
            System.out.println("Подключение к базе установлено");
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
        }
    }

    public void saveAnimal(Animal animal) {

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


                stmt1.executeUpdate();
                stmt2.executeUpdate();

                System.out.println("Животное сохранено в базу");
        } catch (SQLException e) {
            System.err.println("Ошибка при сохранении: " + e.getMessage());
        }
    }


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
                    case "dogs" -> new Dogs(name, birthday, parseCommands(commands));
                    case "cats" -> new Cats(name, birthday, parseCommands(commands));
                    case "hamsters" -> new Hamsters(name, birthday, parseCommands(commands));
                    case "horses" -> new Horses(name, birthday, parseCommands(commands));
                    case "donkeys" -> new Donkeys(name, birthday, parseCommands(commands));
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
        // SQL для обновления таблицы all_animals
        String refreshSql = "INSERT INTO all_animals (name, birthday, commands, source_table) " +
                "SELECT name, birthday, commands, 'dogs' FROM dogs " +
                "UNION " +
                "SELECT name, birthday, commands, 'cats' FROM cats " +
                "UNION " +
                "SELECT name, birthday, commands, 'horses' FROM horses " +
                "UNION " +
                "SELECT name, birthday, commands, 'hamsters' FROM hamsters " +
                "UNION " +
                "SELECT name, birthday, commands, 'donkeys' FROM donkeys " +
                "ON DUPLICATE KEY UPDATE " +
                "birthday = VALUES(birthday), " +
                "commands = VALUES(commands)";


        String selectSql = "SELECT name, birthday, commands, source_table FROM all_animals WHERE name = ?";

        try (
                Statement refreshStmt = connection.createStatement();
                PreparedStatement selectStmt = connection.prepareStatement(selectSql)
        ) {

            refreshStmt.executeUpdate(refreshSql);


            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String animalName = rs.getString("name");
                String commandsStr = rs.getString("commands");
                LocalDate birthday = rs.getDate("birthday").toLocalDate();
                String type = rs.getString("source_table");


                List<String> commands = new ArrayList<>();
                if (commandsStr != null && !commandsStr.isBlank()) {
                    commands = Arrays.asList(commandsStr.split(","));
                }

                Animal animal = switch (type) {
                    case "dogs" -> new Dogs(animalName, birthday, commands);
                    case "cats" -> new Cats(animalName, birthday, commands);
                    case "hamsters" -> new Hamsters(animalName, birthday, commands);
                    case "horses" -> new Horses(animalName, birthday, commands);
                    case "donkeys" -> new Donkeys(animalName, birthday, commands);
                    default -> null;
                };
                    if (animal != null) {
                        System.out.println("Команды животного '" + name + "' (" + animal.getType() + "): " + animal.getCommands());
                    } else {
                        System.out.println(" Не удалось распознать тип животного: " + type);
                    }



            }

        } catch (SQLException e) {
            System.err.println("Ошибка при поиске команд: " + e.getMessage());
        }
    }

    public void trainAnimalCommand(String name, String newCommand) {
        String selectSql = "SELECT commands, source_table FROM all_animals WHERE name = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String existingCommands = rs.getString("commands");
                String type = rs.getString("source_table"); // таблица для UPDATE

                List<String> commandList = new ArrayList<>();
                if (existingCommands != null && !existingCommands.isBlank()) {
                    commandList = new ArrayList<>(Arrays.asList(existingCommands.split(",")));
                }

                if (!commandList.contains(newCommand)) {
                    commandList.add(newCommand);
                }

                String updatedCommands = String.join(",", commandList);

                // правильно формируем запрос
                String updateSql = "UPDATE " + type + " SET commands = ? WHERE name = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setString(1, updatedCommands);
                    updateStmt.setString(2, name);
                    int rows = updateStmt.executeUpdate();

                    if (rows > 0) {
                        System.out.println(" Команда добавлена животному '" + name + "'");
                    } else {
                        System.out.println(" Не удалось обновить команды.");
                    }
                }

            } else {
                System.out.println(" Животное с именем '" + name + "' не найдено в базе.");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при обучении: " + e.getMessage());
        }
    }

}
