package io.github.netbrain.rentalfun.rental;

import io.github.netbrain.rentalfun.customer.Customer;
import io.github.netbrain.rentalfun.customer.CustomerTestData;
import io.github.netbrain.rentalfun.film.Film;
import io.github.netbrain.rentalfun.film.FilmTestData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RentalTestData {

    private final Rental rental;

    /**
     * Creates a new rental object with some default values
     */
    public RentalTestData() {
        this.rental = new Rental(
                new CustomerTestData().build(),
                1,
                new FilmTestData().build()
        );
    }

    public RentalTestData withFilms(Film ... films){
        withFilms(1,films);
        return this;
    }

    public RentalTestData withFilms(int numDays,Film ... films){
        withFilms(numDays,Arrays.asList(films));
        return this;
    }

    public RentalTestData withFilms(int numDays, List<Film> films) {
        rental.setRentalData(films,numDays);
        return this;
    }

    public RentalTestData withCreated(Date date) {
        rental.setCreated(date);
        return this;
    }

    public RentalTestData withCustomer(Customer customer){
        rental.setCustomer(customer);
        return this;
    }

    public Rental build(){
        return rental;
    }
}
