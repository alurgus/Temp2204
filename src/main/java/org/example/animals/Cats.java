package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class Cats extends Animal {
    public Cats(String name, LocalDate birthday, List<String> commands) {
        super(name, birthday, commands);
    }


    @Override
    public String getType() {
        return "кошка";
    }
}


