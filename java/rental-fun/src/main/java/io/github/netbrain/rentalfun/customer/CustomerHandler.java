package io.github.netbrain.rentalfun.customer;

import io.github.netbrain.nicrofw.handler.annotation.Endpoint;

import java.util.List;

public class CustomerHandler {
    private final CustomerRepository customerRepository;

    public CustomerHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Endpoint("/customers")
    public List<Customer> getAll(){
        return customerRepository.all();
    }

    @Endpoint("/customers/:id")
    public Customer getById(int id){
        return customerRepository.getById(id);
    }
}
