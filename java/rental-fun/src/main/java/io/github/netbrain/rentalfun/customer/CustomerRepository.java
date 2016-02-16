package io.github.netbrain.rentalfun.customer;

import io.github.netbrain.rentalfun.core.persistence.Repository;
import org.boon.json.ObjectMapper;

public class CustomerRepository extends Repository<Customer> {
    public CustomerRepository(ObjectMapper json) {
        super(Customer.class, json);
    }
}
