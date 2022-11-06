package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.config.CustomEncoder;
import com.liparistudios.reactspringsecmysql.config.SecurityConfiguration;
import com.liparistudios.reactspringsecmysql.config.Sha256PasswordEncoder;
import com.liparistudios.reactspringsecmysql.config.Sha512PasswordEncoder;
import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Permission;
import com.liparistudios.reactspringsecmysql.model.Role;
import com.liparistudios.reactspringsecmysql.repository.CustomerRepository;
import com.liparistudios.reactspringsecmysql.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
//@Transactional // non sono sicuro
@EnableWebSecurity
public class CustomerServiceImplementation implements CustomerService, UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;


//    @Autowired
//    private SecurityConfiguration securityConfiguration;

    @Autowired
    private CustomEncoder encoder;

//    @Autowired


/*
    public CustomerServiceImplementation(
//        CustomerRepository customerRepository,
//        RoleRepository roleRepository,
//        PasswordEncoder encoder
    ) {
//        this.customerRepository = customerRepository;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = encoder;
    }
*/


    @Override
    public Customer saveCustomer(Customer customerDTO) {


        Customer customer = new Customer();
        if( customerRepository.findByEmail( customerDTO.getEmail() ).isPresent() ) {
            return null;
        };

        if( (customerDTO.getRoles() == null ) || customerDTO.getRoles().isEmpty() ) {
            Role defaultRole = roleRepository
                .findByName("FREE_CUSTOMER")
                .orElseThrow( () -> { throw new IllegalStateException("Ruolo di default non trovato"); })
            ;
            System.out.println("Il customer non ha un ruolo, viene settato quello di default");
            System.out.println( defaultRole.toString() );
            customerDTO.setRoles( new ArrayList<Role>(){{ add(defaultRole); }} );
        }
        else {
            List<Role> allRoles =

                // ruoli corretti corrispondenti ai
                // ruoli senza permessi del customerDTO
                customerDTO
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

            // aggiunta dei ruoli corretti
            allRoles
                .addAll(
                    customerDTO
                        .getRoles()
                        .stream()
                        .filter( role -> (role.getPermissions() != null) && !role.getPermissions().isEmpty() )
                        .map( inconsistentRole -> {
                            return roleRepository.findByName( inconsistentRole.getName() ).orElseThrow( () -> { throw new IllegalStateException("Ruolo di default non trovato"); });
                        })
                        .collect(Collectors.toList())
                )
            ;
            customerDTO.setRoles( allRoles );
        }


        if( (customer.getRoles() == null ) || customer.getRoles().isEmpty() ) {
            customer.setRoles( customerDTO.getRoles() );
        }


        customer.setPassword( encoder.passwordEncoder().encode( customerDTO.getPassword() ) );

        customer.setDob( customerDTO.getDob() );
        customer.setEmail( customerDTO.getEmail() );
        customer.setPhoneNumber( customerDTO.getPhoneNumber() );


        System.out.println("controllo password encoder");
        System.out.println("abc = ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        System.out.println(encoder.passwordEncoder().encode("abc"));


        System.out.println("password arrivata");
        System.out.println(customerDTO.getPassword());
        System.out.println("password arrivata encripted");
        System.out.println( encoder.passwordEncoder().encode(customerDTO.getPassword()));
        System.out.println("password da salvare (encripted)");
        System.out.println( customer.getPassword() );



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
        PageRequest page = PageRequest.of( pageIndex, 255 );  // page, size
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

    @Override
    public Long count() {
        return Long.valueOf( customerRepository.count() );
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // public Customer loadUserByUsername(String username) throws UsernameNotFoundException {


        System.out.println("loadUserByUsername {"+ username +"}");

        return
            customerRepository
                .findByEmail( username )
                .map( Customer::new )
                .orElseThrow( () -> new UsernameNotFoundException("email non esiste") )
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
