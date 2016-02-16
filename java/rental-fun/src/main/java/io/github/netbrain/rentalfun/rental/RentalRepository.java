package io.github.netbrain.rentalfun.rental;

import io.github.netbrain.rentalfun.core.persistence.Repository;
import org.boon.json.ObjectMapper;

/**
 * This file would usually have extended functionality on Rental objects
 * e.g getByCustomers,
 */
public class RentalRepository extends Repository<Rental> {
    public RentalRepository(ObjectMapper json) {
        super(Rental.class, json);
    }
}
