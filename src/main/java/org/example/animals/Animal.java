package org.example.animals;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public abstract class Animal {
    private String name;
    private LocalDate birthday;
    private List<String> commands;

    public Animal(String name, LocalDate birthday) {
        this.name = name;
        this.birthday = birthday;
        this.commands = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public int getAgeInMonths() {
        return Period.between(birthday, LocalDate.now()).getYears() * 12 +
                Period.between(birthday, LocalDate.now()).getMonths();
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addCommand(String command) {
        if (!commands.contains(command)) {
            commands.add(command);
        }
    }

    public void train(String command) {
        commands.add(command);
    }

    @Override
    public String toString() {
        return name + " (" + birthday + ") — Команды: " + commands;
    }
}
