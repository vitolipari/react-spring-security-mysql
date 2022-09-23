package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.repository.CustomerRepository;
import com.liparistudios.reactspringsecmysql.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImplementation implements CustomerService, UserDetailsService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

    // TODO da finire
    //  https://www.youtube.com/watch?v=VVn9OG9nfH0&t=546s
    //  video al minuto 1:22:22



    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save( customer );
    }

    @Override
    public void addRoleToCustomer(String email, String roleName) {

        Optional<Customer> customer = customerRepository.findByEmail( email );
        if( customer.isPresent() ) {
            Optional<Role> roleToFind = roleRepository.findByName( roleName );
            if( roleToFind.isPresent() ) {
                customer.get().setRole( roleToFind.get() );
            }
            else {
                // ruolo inesistente
                throw new IllegalStateException("ruolo inesistente");
            }

        }
        else {
            // customer inesistente
            throw new IllegalStateException("email inesistente");
        }

    }

    @Override
    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> searchedCustomer = customerRepository.findByEmail( username );
        Customer customer = null;
        if( !searchedCustomer.isPresent() ) {
            throw new UsernameNotFoundException("email non esiste");
        }
        else {
            customer = searchedCustomer.get();
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add( new SimpleGrantedAuthority( customer.getRole().getName() ));
        return new User( username, customer.getPassword(), authorities );
    }
}
