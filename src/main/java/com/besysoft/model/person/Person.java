package com.besysoft.model.person;

import com.besysoft.common.errorHandler.person.InvalidPersonException;

import java.util.UUID;

public abstract class Person {

    private final UUID personId;
    private final String code;
    private final String name;

    public Person(String code, String name) {
        if (code == null || code.isBlank()) {
            throw new InvalidPersonException(
                    "code",
                    "El código no puede ser nulo ni estar vacío."
            );
        }

        if (name == null || name.isBlank()) {
            throw new InvalidPersonException(
                    "name",
                    "El nombre no puede ser nulo ni estar vacío."
            );
        }
        this.personId = UUID.randomUUID();
        this.code = code.trim();
        this.name = name.trim();
    }

    public UUID personId(){
        return this.personId;
    }
    public String code() {
        return this.code;
    }

    public String name() {
        return this.name;
    }
}
