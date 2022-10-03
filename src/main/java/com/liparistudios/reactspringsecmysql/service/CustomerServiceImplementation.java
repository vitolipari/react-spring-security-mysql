package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Permission;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImplementation implements CustomerService/*, UserDetailsService*/ {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

    // TODO da finire
    //  https://www.youtube.com/watch?v=VVn9OG9nfH0&t=546s
    //  video al minuto 1:22:22



    @Override
    public Customer saveCustomer(Customer customer) {
        // controllo ruoli
        if( customer.getRoles().isEmpty() ) {
            throw new IllegalStateException("Customer senza ruoli");
        }
        return customerRepository.save( customer );
    }

    @Override
    public void addRoleToCustomer(String email, String roleName) {

        Optional<Customer> customer = customerRepository.findByEmail( email );
        if( customer.isPresent() ) {
            Optional<Role> roleToFind = roleRepository.findByName( roleName );
            if( roleToFind.isPresent() ) {
                customer.get().getRoles().add( roleToFind.get() );
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
    public List<Permission> getAllPermissions(String email) {
        return
            customerRepository
                .findByEmail( email )
                .orElseThrow( () -> {
                    throw new IllegalStateException("Email not exist");
                })
                .getRoles()
                .stream()
                .map( role -> role.getPermissions() )
                .flatMap( permissions -> permissions.stream() /*Stream.of( permissions )*/ )
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toList())
        ;

    }

    @Override
    public List<Permission> getAllPermissions(Long id) {
        return
            customerRepository
                .findById( id )
                .orElseThrow( () -> {
                    throw new IllegalStateException("Email not exist");
                })
                .getRoles()
                .stream()
                .map( role -> role.getPermissions() )
                .flatMap( permissions -> permissions.stream() /*Stream.of( permissions )*/ )
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toList())
            ;
    }

    @Override
    public List<Permission> getAllPermissions(Customer customer) {
        return getAllPermissions( customer.getId() );
    }


    public Customer loadCustomerByEmail(String email) throws UsernameNotFoundException {
        return loadCustomerByUsername( email );
    }


    public Customer loadCustomerByUsername(String username) throws UsernameNotFoundException {
        return
            customerRepository.findByEmail(username)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("email non esiste");
                })
            ;
    }

    /*
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
        authorities.add( new SimpleGrantedAuthority( customer.getRoles().getName() ));
        return new User( username, customer.getPassword(), authorities );
    }
    */

}
