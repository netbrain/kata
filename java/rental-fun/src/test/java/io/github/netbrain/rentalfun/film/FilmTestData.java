package io.github.netbrain.rentalfun.film;

public class FilmTestData {

    private final Film film;

    public FilmTestData() {
        film = new Film("Testfilm", Film.Type.REGULAR);
    }

    public FilmTestData withRented(boolean rented){
        film.setRented(rented);
        return this;
    }

    public FilmTestData withType(Film.Type type){
        film.setType(type);
        return this;
    }

    public FilmTestData withTitle(String title){
        film.setTitle(title);
        return this;
    }

    public Film build() {
        return film;
    }
}