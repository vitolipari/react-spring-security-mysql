package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.repository.CustomerRepository;
import com.liparistudios.reactspringsecmysql.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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


        if( customerRepository.findByEmail( customer.getEmail() ).isPresent() ) {
            return null;
        };

        if( (customer.getRoles() == null ) || customer.getRoles().isEmpty() ) {
            Role defaultRole = roleRepository
                .findByName("FREE_CUSTOMER")
                .orElseThrow( () -> { throw new IllegalStateException("Ruolo di default non trovato"); })
            ;
            System.out.println("Il customer non ha un ruolo, viene settato quello di default");
            System.out.println( defaultRole.toString() );
            customer.setRoles( new ArrayList<Role>(){{ add(defaultRole); }} );
        }
        else {
            List<Role> allRoles =
                customer
                    .getRoles()
                        .stream()
                        .filter( role -> ((role.getPermissions() == null) || role.getPermissions().isEmpty()) )
                        .map( inconsistentRole -> {
                            return
                                roleRepository
                                    .findByName( inconsistentRole.getName() )
                                    .orElseGet( () -> {
                                        return
                                            roleRepository
                                                .findByName("FREE_CUSTOMER")
                                                .get()
                                        ;
                                    })
                            ;
                        })
                        .collect(Collectors.toList())
            ;
            allRoles
                .addAll(
                    customer
                        .getRoles()
                        .stream()
                        .filter( role -> (role.getPermissions() != null) && !role.getPermissions().isEmpty() )
                        .map( inconsistentRole -> {
                            return roleRepository.findByName( inconsistentRole.getName() ).orElseThrow( () -> { throw new IllegalStateException("Ruolo di default non trovato"); });
                        })
                        .collect(Collectors.toList())
                )
            ;
            customer.setRoles( allRoles );
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

    public List<Customer> getAllCustomer( Integer pageIndex ) {
        PageRequest page = PageRequest.of( pageIndex +1, 255 );  // page, size
        return customerRepository.findAll( page ).getContent();     // .toList()
    }

    @Override
    public List<Customer> getAllCustomer() {
        PageRequest page = PageRequest.of( 1, 255 );  // page, size
        return customerRepository.findAll( page ).getContent();     // .toList()
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

    public Customer loadCustomerById(Long id) throws UsernameNotFoundException {
        return
            customerRepository.findById( id )
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
