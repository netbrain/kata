package io.github.netbrain.rentalfun.rental;

import io.github.netbrain.rentalfun.core.persistence.Entity;
import io.github.netbrain.rentalfun.customer.Customer;
import io.github.netbrain.rentalfun.film.Film;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class Rental extends Entity{

    private final static int PREMIUM_PRICE = 40;
    private final static int BASIC_PRICE = 30;

    private int customerId;
    private int numDays;
    private int price;
    private int[] films;

    private Surcharge surcharge = null;

    public Rental(Customer customer, int numDays, Film ... films) {
        this(customer,numDays, Arrays.asList(films));
    }

    public Rental(Customer customer, int numDays, List<Film> films) {
        setCustomer(customer);
        setNumDays(numDays);
        setRentalData(films,numDays);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomer(Customer customer) {
        if(customer == null){
            throw new IllegalArgumentException("Customer cannot be null");
        }
        setCustomer(customer.getId());
    }

    public void setCustomer(int customerId) {
        this.customerId = customerId;
    }

    public int[] getFilms() {
        return films;
    }

    public int getNumDays() {
        return numDays;
    }

    public int getPrice() {
        return price;
    }

    public void setRentalData(List<Film> films, int numDays) {
        this.films = films.stream().mapToInt(Entity::getId).toArray();
        setNumDays(numDays);
        this.price = calculatePrice(films,numDays);
    }

    public void setSurcharge(Surcharge surcharge) {
        this.surcharge = surcharge;
    }

    private void setNumDays(int numDays) {
        if(numDays < 0){
            throw new IllegalArgumentException("Can't have a negatively valued number of days");
        }
        this.numDays = numDays;
    }

    public Surcharge getSurcharge() {
        return surcharge;
    }

    private static int calculatePrice(List<Film> films, long numDays){
        int price = 0;
        if(numDays > 0) {
            for (Film film : films) {
                switch (film.getType()) {
                    case NEW:
                        price += PREMIUM_PRICE * numDays;
                        break;
                    case REGULAR:
                        price += BASIC_PRICE * Math.min(1, numDays) + BASIC_PRICE * Math.max(0, numDays - 3);
                        break;
                    case OLD:
                        price += BASIC_PRICE * Math.min(1, numDays) + BASIC_PRICE * Math.max(0, numDays - 5);
                        break;
                }
            }
        }
        return price;
    }

    public static class Surcharge {
        private long extraDays;
        private int amount;

        public Surcharge(Rental rental, List<Film> films) {
            Instant from = rental.getCreated().toInstant();
            Instant to = Instant.now();
            long days  = from.until(to, ChronoUnit.DAYS)-rental.getNumDays();
            extraDays = Math.max(0,days);
            if(extraDays < 0){
                throw new IllegalArgumentException("Can't have a negatively valued number of days");
            }
            this.amount = Rental.calculatePrice(films, extraDays);
        }

        public long getExtraDays() {
            return extraDays;
        }

        public int getAmount() {
            return amount;
        }
    }
}
