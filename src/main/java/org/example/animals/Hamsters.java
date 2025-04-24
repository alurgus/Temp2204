package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class Hamsters extends Animal {
    public Hamsters(String name, LocalDate birthday, List<String> commands) {
        super(name, birthday, commands);
    }
}

