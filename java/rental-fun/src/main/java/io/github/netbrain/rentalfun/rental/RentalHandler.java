package io.github.netbrain.rentalfun.rental;


import io.github.netbrain.nicrofw.handler.annotation.Endpoint;
import io.github.netbrain.nicrofw.request.RequestHeaders;
import io.github.netbrain.rentalfun.customer.Customer;
import io.github.netbrain.rentalfun.customer.CustomerRepository;
import io.github.netbrain.rentalfun.film.Film;
import io.github.netbrain.rentalfun.film.FilmRepository;

import java.util.List;

import static fi.iki.elonen.NanoHTTPD.Method.DELETE;
import static fi.iki.elonen.NanoHTTPD.Method.POST;

public class RentalHandler {

    private final CustomerRepository customerRepository;
    private final RentalRepository rentalRepository;
    private final FilmRepository filmRepository;

    public RentalHandler(CustomerRepository customerRepository, RentalRepository rentalRepository, FilmRepository filmRepository) {
        this.customerRepository = customerRepository;
        this.rentalRepository = rentalRepository;
        this.filmRepository = filmRepository;
    }

    private int getCustomerId(RequestHeaders headers) {
        String customer = headers.get("X-Customer");
        if(customer == null){
            throw new IllegalArgumentException("Expected a customer id");
        }
        return Integer.parseInt(customer);
    }

    /**
     * Retrieves all rentals in the system.
     * @return
     */
    @Endpoint(value = "/rentals")
    public List<Rental> getRentals(){
        return rentalRepository.all();
    }

    /**
     * Retrieves a single rental in the system.
     * @return
     */
    @Endpoint(value = "/rentals/:id")
    public Rental getRental(int id){
        return rentalRepository.getById(id);
    }

    /**
     * Creates a rental, this is the action performed by a customer when renting one or several videos.
     * Fetches the films and makes sure they are not already rented by another customer, then reserves all
     * rented films making them unavailable to all other customers and returns a rental object on the wire wich
     * details the rental details with price, number of days, films etc.
     * @param headers
     * @param rentalRequest
     * @return
     */
    @Endpoint(value = "/rentals", method = POST)
    public Rental rentFilms(RequestHeaders headers, RentalRequest rentalRequest){
        int customerId = getCustomerId(headers);
        int[] filmIds = rentalRequest.getFilms();
        int numDays = rentalRequest.getNumDays();

        List<Film> films = filmRepository.getByIds(filmIds);
        if (films.stream().filter(Film::isRented).count() > 0){
            throw new IllegalArgumentException("Film already rented!");
        }

        Customer customer = customerRepository.getById(customerId);
        if(customer == null){
            throw new IllegalArgumentException("Customer is required");
        }

        Rental rental = new Rental(customer,numDays,films);
        films.stream().forEach(film -> {
            film.setRented(true);
            filmRepository.update(film);
        });
        rentalRepository.insert(rental);

        customer.addBonus(films);
        customerRepository.update(customer);

        return rental;
    }

    /**
     * When a customer is returning a set of rented videos.
     * Doesn't actually delete anything, just updates the rental record with a surcharge object, and returns the entire
     * rental data back on the wire. Additionally this makes all films available to other customers.
     * @param id
     * @param headers
     * @return
     */
    @Endpoint(value = "/rentals/:id", method = DELETE)
    public Rental returnFilms(int id, RequestHeaders headers){
        int customerId = getCustomerId(headers);

        Rental rental = rentalRepository.getById(id);
        if(customerId != rental.getCustomerId()){
            throw new RuntimeException("Customer can't access another customer's records");
        }

        List<Film> films = filmRepository.getByIds(rental.getFilms());
        rental.setSurcharge(new Rental.Surcharge(rental, films));
        rentalRepository.update(rental);

        films.stream().forEach(film -> {
            film.setRented(false);
            filmRepository.update(film);
        });

        return rental;
    }
}
