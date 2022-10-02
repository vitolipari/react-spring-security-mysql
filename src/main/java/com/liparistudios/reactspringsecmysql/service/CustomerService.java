package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Permission;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CustomerService {

    Customer saveCustomer(Customer customer);

    void addRoleToCustomer( String username, String roleName );

    List<Customer> getAllCustomer();

    List<Permission> getAllPermissions( String email );
    List<Permission> getAllPermissions( Long id );
    List<Permission> getAllPermissions( Customer customer );


}
