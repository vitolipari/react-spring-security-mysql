package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.SystemUserDetails;
import com.liparistudios.reactspringsecmysql.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SystemUserDetailsService implements UserDetailsService {

    @Autowired private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return
            new SystemUserDetails(
                customerRepository
                    .findByEmail( username )
                    .orElseThrow( () -> new UsernameNotFoundException("email doesn't exists") )
            )
        ;
    }


}
