package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class AnimalFactory {
    public static Animal create(String type, String name, LocalDate birth, List<String> commands) {
        Animal animal = switch (type.toLowerCase()) {
            case "dog" -> new Dog(name, birth);
            case "cat" -> new Cat(name, birth);
            case "hamster" -> new Hamster(name, birth);
            case "horse" -> new Horse(name, birth);
            case "donkey" -> new Donkey(name, birth);
            default -> null;
        };



        if (animal != null) {
            for (String cmd : commands) {
                animal.train(cmd);
            }
        }

        return animal;
    }



}
