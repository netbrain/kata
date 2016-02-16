package io.github.netbrain.rentalfun.film;


import io.github.netbrain.rentalfun.core.persistence.Repository;
import org.boon.json.ObjectMapper;

public class FilmRepository extends Repository<Film> {
    public FilmRepository(ObjectMapper json) {
        super(Film.class, json);
    }
}
