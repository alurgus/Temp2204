package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class Donkeys extends Animal {
    public Donkeys(String name, LocalDate birthday, List<String> commands) {
        super(name, birthday, commands);
    }

    @Override
    public String getType() {
        return "осел";
    }
}

