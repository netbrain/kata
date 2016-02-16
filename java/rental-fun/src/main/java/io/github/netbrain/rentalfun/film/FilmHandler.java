package io.github.netbrain.rentalfun.film;


import io.github.netbrain.nicrofw.handler.annotation.Endpoint;

import java.util.List;

public class FilmHandler {

    private final FilmRepository filmRepository;

    public FilmHandler(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Endpoint("/films")
    public List<Film> getAll(){
        return filmRepository.all();
    }

    @Endpoint("/films/:id")
    public Film getById(int id){
        return filmRepository.getById(id);
    }
}
