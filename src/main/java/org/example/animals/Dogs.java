package org.example.animals;

import java.time.LocalDate;
import java.util.List;

public class Dogs extends Animal {
    public Dogs(String name, LocalDate birthday, List<String> commands) {
        super(name, birthday, commands);
    }

    @Override
    public String getType() {
        return "собака";
    }
}