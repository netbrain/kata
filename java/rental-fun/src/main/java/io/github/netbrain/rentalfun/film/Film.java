package io.github.netbrain.rentalfun.film;

import io.github.netbrain.rentalfun.core.persistence.Entity;

public class Film extends Entity{

    private String title;
    private Type type;
    private boolean rented;

    public Film(String title, Type type) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public enum Type {
        NEW,
        REGULAR,
        OLD
    }
}
