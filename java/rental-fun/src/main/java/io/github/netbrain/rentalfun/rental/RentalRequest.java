package io.github.netbrain.rentalfun.rental;


public class RentalRequest {
    private int[] films;
    private int numDays;

    public RentalRequest(int[] films, int numDays) {
        this.films = films;
        this.numDays = numDays;
    }

    public int[] getFilms() {
        return films;
    }

    public int getNumDays() {
        return numDays;
    }
}
