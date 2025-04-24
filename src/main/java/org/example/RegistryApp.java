package org.example;

import org.example.animals.*;
import org.example.database.DatabaseService;
import org.example.utils.Counter;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

import java.io.InputStream;
import java.util.Properties;

public class RegistryApp {
    private static List<Animal> animals = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static DatabaseService db;

    public class Config {
        public static Properties load() {
            Properties props = new Properties();
            try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    throw new RuntimeException("Файл config.properties не найден");
                }
                props.load(input);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка загрузки config.properties: " + e.getMessage());
            }
            return props;
        }
    }

    public static void main(String[] args) {

        Properties config = Config.load(); // загружаем config.properties
        System.out.println("Connecting to: " + config.getProperty("db.url"));

         db = new DatabaseService(
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password")
        );


        boolean running = true;

        while (running) {
            showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addNewAnimal();
                case "2" -> showAnimalCommands();
                case "3" -> trainAnimal();
                case "4" -> listAllAnimals();
                case "0" -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
        System.out.println("Завершение программы.");
    }

    private static void showMenu() {
        System.out.println("""
                === Реестр животных ===
                1. Завести новое животное
                2. Посмотреть команды животного
                3. Обучить животное новым командам
                4. Показать всех животных
                0. Выход
                Выберите действие:""");
    }

    private static void addNewAnimal() {
        try (Counter counter = new Counter()) {
            System.out.println("Введите имя животного:");
            String name = scanner.nextLine();

            System.out.println("Введите дату рождения (ГГГГ-ММ-ДД):");
            LocalDate birthday = LocalDate.parse(scanner.nextLine());


            /*System.out.println("Выберите тип животного: dog / cat / hamster / horse / donkey");
            String type = scanner.nextLine().toLowerCase();*/

            Map<String, String> typeMap = Map.of(
                    "1", "dogs",
                    "2", "cats",
                    "3", "hamsters",
                    "4", "horses",
                    "5", "donkeys"
            );

            System.out.println("""
                    Выберите тип животного:
                    1. Dog
                    2. Cat
                    3. Hamster
                    4. Horse
                    5. Donkey
                    """);

            String input = scanner.nextLine().toLowerCase();
            String type = typeMap.getOrDefault(input, input); // если введена строка (dog), то останется как есть

            List<String> commands = new ArrayList<>();
            System.out.println("Введите команды (по одной на строку, пустая строка — завершение):");
            while (true) {
                String cmd = scanner.nextLine();
                if (cmd.isBlank()) break;
                commands.add(cmd);
            }

            Animal animal = switch (type) {
                case "dogs" -> new Dogs(name, birthday, commands);
                case "cats" -> new Cats(name, birthday, commands);
                case "hamsters" -> new Hamsters(name, birthday, commands);
                case "horses" -> new Horses(name, birthday, commands);
                case "donkeys" -> new Donkeys(name, birthday, commands);
                default -> {
                    System.out.println("Неизвестный тип.");
                    yield null;
                }
            };

            if (animal != null) {
                animals.add(animal);
                db.saveAnimal(animal);
                counter.add(); // увеличиваем счётчик
                System.out.println("Животное добавлено: " + animal.getName());
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void showAnimalCommands() {
        System.out.println("Введите имя животного:");
        String name = scanner.nextLine();

        db.showCommandsByName(name);
        /*Animal animal = findAnimalByName(name);*/

        /*if (animal != null) {
            System.out.println("Команды: " + animal.getCommands());
        } else {
            System.out.println("Животное не найдено.");
        }*/
    }

    private static void trainAnimal() {
        System.out.println("Введите имя животного:");
        String name = scanner.nextLine();

        System.out.println("Введите новую команду:");
        String cmd = scanner.nextLine();
        db.trainAnimalCommand(name, cmd);

        /*Animal animal = findAnimalByName(name);
        if (animal != null) {
            System.out.println("Введите новую команду:");
            String cmd = scanner.nextLine();
            animal.addCommand(cmd);
            System.out.println("Команда добавлена.");
        } else {
            System.out.println("Животное не найдено.");
        }*/
    }

    private static void listAllAnimals() {
        if (animals.isEmpty()) {
            System.out.println("Реестр пуст.");
        } else {
            animals.forEach(System.out::println);
        }
    }

    private static Animal findAnimalByName(String name) {
        return animals.stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

}
