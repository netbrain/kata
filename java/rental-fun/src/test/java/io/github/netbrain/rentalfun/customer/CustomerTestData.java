package io.github.netbrain.rentalfun.customer;


public class CustomerTestData {
    private final Customer customer;

    public CustomerTestData() {
        this.customer = new Customer("testcustomer");
    }

    public CustomerTestData withUsername(String username){
        this.customer.setUsername(username);
        return this;
    }

    public Customer build() {
        return customer;
    }
}
