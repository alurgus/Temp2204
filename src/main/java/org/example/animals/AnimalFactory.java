package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class AnimalFactory {
    public static Animal create(String type, String name, LocalDate birth, List<String> commands) {
        Animal animal = switch (type.toLowerCase()) {
            case "dogs" -> new Dogs(name, birth, commands);
            case "cats" -> new Cats(name, birth, commands);
            case "hamsters" -> new Hamsters(name, birth, commands);
            case "horses" -> new Horses(name, birth, commands);
            case "donkeys" -> new Donkeys(name, birth, commands);
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
