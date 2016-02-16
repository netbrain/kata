package io.github.netbrain.rentalfun.customer;

import io.github.netbrain.rentalfun.core.persistence.Entity;
import io.github.netbrain.rentalfun.film.Film;

import java.util.List;

public class Customer extends Entity{

    private String username;

    private int bonus;

    public Customer(String username) {
        setUsername(username);
    }

    public void setUsername(String username) {
        if(username == null || username.length() == 0){
            throw new IllegalArgumentException("Invalid username");
        }
        this.username = username;
    }

    public void addBonus(List<Film> films) {
        films.stream().map(Film::getType).forEach(type -> {
            switch (type){
                case NEW:
                    bonus += 2;
                    break;
                case OLD:
                case REGULAR:
                    bonus++;
                    break;
                default:
                    throw new RuntimeException("Unknown film type");
            }
        });
    }
}
