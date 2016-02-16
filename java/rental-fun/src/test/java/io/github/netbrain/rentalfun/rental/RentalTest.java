package io.github.netbrain.rentalfun.rental;


import io.github.netbrain.rentalfun.film.Film;
import io.github.netbrain.rentalfun.film.Film.Type;
import io.github.netbrain.rentalfun.film.FilmTestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.github.netbrain.rentalfun.film.Film.Type.*;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;

public class RentalTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCannotSetDaysToANegativeNumber() throws Exception {
        new RentalTestData().withFilms(-1).build();
    }

    @RunWith(Parameterized.class)
    public static class PriceTest {

        private final Rental rental;
        private final int expectedPrice;

        @Parameters(name = "Renting a {0} film for {1} days = {2} SEK")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {NEW,0,40*0},
                    {NEW,1,40*1},
                    {NEW,2,40*2},
                    {NEW,3,40*3},
                    {NEW,4,40*4},
                    {NEW,5,40*5},
                    {NEW,6,40*6},
                    {NEW,7,40*7},
                    {REGULAR,0,0},
                    {REGULAR,1,30},
                    {REGULAR,2,30},
                    {REGULAR,3,30},
                    {REGULAR,4,30+(30*(4-3))},
                    {REGULAR,5,30+(30*(5-3))},
                    {REGULAR,6,30+(30*(6-3))},
                    {REGULAR,7,30+(30*(7-3))},
                    {OLD,0,0},
                    {OLD,1,30},
                    {OLD,2,30},
                    {OLD,3,30},
                    {OLD,4,30},
                    {OLD,5,30},
                    {OLD,6,30+(30*(6-5))},
                    {OLD,7,30+(30*(7-5))},
            });

        }

        public PriceTest(Type filmType, int numDays, int expectedPrice) {
            this.expectedPrice = expectedPrice;

            Film film = new FilmTestData()
                    .withType(filmType)
                    .build();

            rental = new RentalTestData()
                    .withFilms(numDays,film)
                    .build();
        }

        @Test
        public void testPricing() throws Exception {
            assertEquals("Pricing is wrong",expectedPrice,rental.getPrice());
        }

    }


}