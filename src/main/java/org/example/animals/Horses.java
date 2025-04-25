package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class Horses extends Animal {
    public Horses(String name, LocalDate birthday, List<String> commands) {
        super(name, birthday, commands);
    }

    @Override
    public String getType() {
        return "лошадь";
    }
}

