package io.github.netbrain.rentalfun.rental;

import io.github.netbrain.rentalfun.customer.Customer;
import io.github.netbrain.rentalfun.customer.CustomerRepository;
import io.github.netbrain.rentalfun.customer.CustomerTestData;
import io.github.netbrain.rentalfun.film.Film;
import io.github.netbrain.rentalfun.film.FilmRepository;
import io.github.netbrain.rentalfun.film.FilmTestData;
import io.github.netbrain.rentalfun.test.IntegrationTest;
import io.github.netbrain.rentalfun.test.TestRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.github.netbrain.rentalfun.film.Film.Type.*;
import static org.junit.Assert.*;


public class ApplicationIntegrationTest extends IntegrationTest {

    private final CustomerRepository customerRepository = singletons.get(CustomerRepository.class);
    private final FilmRepository filmRepository = singletons.get(FilmRepository.class);
    private final RentalRepository rentalRepository = singletons.get(RentalRepository.class);

    private Customer createCustomer() {
        Customer customer = new CustomerTestData().build();
        customerRepository.insert(customer);
        return customer;
    }

    private List<Film> createFilms() {
        List<Film> films = Arrays.asList(
                new FilmTestData()
                        .withTitle("The Matrix 11")
                        .withType(NEW)
                        .build(),
                new FilmTestData()
                        .withTitle("Spider Man")
                        .withType(REGULAR)
                        .build(),
                new FilmTestData()
                        .withTitle("Pulp Fiction")
                        .withType(OLD)
                        .build()
        );

        films.stream().forEach(filmRepository::insert);
        return films;
    }

    @Test
    public void testRetrieveCustomers() throws Exception {
        Customer customer = createCustomer();

        byte[] response = TestRequest.create("GET","/customers").execute();
        List<Customer> customers = (List<Customer>)json.readValue(response, List.class, Customer.class);
        assertNotNull(customers);
        assertTrue(customers.size() == 1);
        assertEquals(customer,customers.get(0));
    }

    @Test
    public void testRetrieveASingleCustomer() throws Exception {
        Customer customer = createCustomer();

        byte[] response = TestRequest.create("GET","/customers/0").execute();
        Customer otherCustomer = json.readValue(response, Customer.class);
        assertNotNull(otherCustomer);
        assertEquals(customer,otherCustomer);
    }

    @Test
    public void testRetrieveFilms() throws Exception {
        List<Film> films = createFilms();

        byte[] response = TestRequest.create("GET","/films").execute();
        List<Film> otherFilms = (List<Film>)json.readValue(response, List.class, Film.class);
        assertNotNull(otherFilms);
        assertTrue(films.size() == 3);
        assertTrue(CollectionUtils.isEqualCollection(films,otherFilms));
    }

    @Test
    public void testRetrieveASingleFilm() throws Exception {
        List<Film> films = createFilms();

        byte[] response = TestRequest.create("GET","/films/0").execute();
        Film otherFilm = json.readValue(response, Film.class);
        assertNotNull(otherFilm);
        assertEquals(films.get(0),otherFilm);
    }

    @Test
    public void testCanRentFilms() throws Exception {
        Customer customer = createCustomer();
        createFilms();

        int numDays = 14;
        int[] ids = {0, 1};
        byte[] response = TestRequest.create("POST","/rentals")
                .withHeader("X-Customer",String.valueOf(customer.getId()))
                .withData(json.writeValueAsBytes(
                        new RentalRequest(ids, numDays)
                ))
                .execute();

        Rental rental = json.readValue(response, Rental.class);
        assertEquals(customer.getId(),rental.getCustomerId());
        assertArrayEquals(ids,rental.getFilms());
        assertEquals(numDays,rental.getNumDays());
        assertEquals(920,rental.getPrice());
        assertNull(rental.getSurcharge());
    }

    @Test
    public void testCanReturnFilms() throws Exception {
        Customer customer = createCustomer();
        List<Film> films = createFilms();

        //14 days ago
        Date date = Date.from(
                LocalDate.now()
                        .minusDays(14)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        //Rental with 14 days expectancy
        Rental rental = new RentalTestData()
                .withCustomer(customer)
                .withFilms(14,films)
                .withCreated(date)
                .build();

        rentalRepository.insert(rental);

        byte[] response = TestRequest.create("DELETE","/rentals/0")
                .withHeader("X-Customer",String.valueOf(customer.getId()))
                .execute();

        Rental otherRental = json.readValue(response, Rental.class);
        assertEquals(0,otherRental.getSurcharge().getExtraDays());
        assertEquals(0,otherRental.getSurcharge().getAmount());
    }



    @Test
    public void testCanReturnFilmsOverdue() throws Exception {
        Customer customer = createCustomer();
        List<Film> films = createFilms();

        //30 days ago
        Date date = Date.from(
                LocalDate.now()
                        .minusDays(7)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()
        );

        //Rental with 14 days expectancy
        Rental rental = new RentalTestData()
                .withCustomer(customer)
                .withFilms(5,films)
                .withCreated(date)
                .build();

        rentalRepository.insert(rental);

        byte[] response = TestRequest.create("DELETE","/rentals/0")
                .withHeader("X-Customer",String.valueOf(customer.getId()))
                .execute();

        Rental otherRental = json.readValue(response, Rental.class);
        assertEquals(2,otherRental.getSurcharge().getExtraDays());
        assertEquals(140,otherRental.getSurcharge().getAmount());
    }

}