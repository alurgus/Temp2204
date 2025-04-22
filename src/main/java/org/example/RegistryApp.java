package org.example;

import org.example.animals.*;
import org.example.utils.Counter;

import java.time.LocalDate;
import java.util.*;

public class RegistryApp {
    private static List<Animal> animals = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
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

            System.out.println("Выберите тип животного: dog / cat / hamster / horse / donkey");
            String type = scanner.nextLine().toLowerCase();

            Animal animal = switch (type) {
                case "dog" -> new Dog(name, birthday);
                case "cat" -> new Cat(name, birthday);
                case "hamster" -> new Hamster(name, birthday);
                case "horse" -> new Horse(name, birthday);
                case "donkey" -> new Donkey(name, birthday);
                default -> {
                    System.out.println("Неизвестный тип.");
                    yield null;
                }
            };

            if (animal != null) {
                animals.add(animal);
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

        Animal animal = findAnimalByName(name);
        if (animal != null) {
            System.out.println("Команды: " + animal.getCommands());
        } else {
            System.out.println("Животное не найдено.");
        }
    }

    private static void trainAnimal() {
        System.out.println("Введите имя животного:");
        String name = scanner.nextLine();

        Animal animal = findAnimalByName(name);
        if (animal != null) {
            System.out.println("Введите новую команду:");
            String cmd = scanner.nextLine();
            animal.addCommand(cmd);
            System.out.println("Команда добавлена.");
        } else {
            System.out.println("Животное не найдено.");
        }
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
