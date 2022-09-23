package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Customer;
import org.springframework.stereotype.Service;


public interface CustomerService {

    Customer saveCuustomer(Customer customer);

    void addRoleToCustomer( String username, String roleName );

}
