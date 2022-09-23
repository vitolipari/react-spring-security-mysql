package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Customer;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CustomerService {

    Customer saveCustomer(Customer customer);

    void addRoleToCustomer( String username, String roleName );

    List<Customer> getAllCustomer();

}
