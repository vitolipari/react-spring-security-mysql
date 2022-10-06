package com.liparistudios.reactspringsecmysql.repository;

import com.liparistudios.reactspringsecmysql.model.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {


    Optional<Customer> findByEmail(String email);


}
