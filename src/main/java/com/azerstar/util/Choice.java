package com.azerstar.util;

/** ComboBox üçün: id və göstərilən mətn. */
public record Choice(int id, String label) {

    @Override
    public String toString() {
        return label;
    }
}
