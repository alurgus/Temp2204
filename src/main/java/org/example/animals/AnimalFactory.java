package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class AnimalFactory {
    public static Animal create(String type, String name, LocalDate birth, List<String> commands) {
        Animal animal = switch (type.toLowerCase()) {
            case "dogs" -> new Dogs(name, birth);
            case "cats" -> new Cats(name, birth);
            case "hamsters" -> new Hamsters(name, birth);
            case "horses" -> new Horses(name, birth);
            case "donkeys" -> new Donkeys(name, birth);
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
