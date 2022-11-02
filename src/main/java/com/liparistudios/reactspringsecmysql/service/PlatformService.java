package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Platform;
import com.liparistudios.reactspringsecmysql.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    @Autowired
    public PlatformService( PlatformRepository repo ) {
        this.platformRepository = repo;
    }


    public List<Platform> getAllPlatform() {
        PageRequest page = PageRequest.of( 1, 255 );  // page, size
        return platformRepository.findAll( page ).getContent();
    }


    public List<Platform> getPlatforms( Integer pageIndex ) {
        PageRequest page = PageRequest.of( pageIndex, 255 );  // page, size
        return platformRepository.findAll( page ).getContent();
    }

    public Platform addPlatform( Platform platform ) {
        return platformRepository.save( platform );
    }

    public Platform getPlatformById( Long id ) {
        return platformRepository
            .findById( id )
            .orElseThrow( () -> {
               throw new IllegalStateException("PlatformID non associata a nessuna piattaforma");
            })
        ;
    }

    public Platform getPlatformByName( String name ) {
        return platformRepository
            .findByName( name )
            .orElseThrow( () -> {
               throw new IllegalStateException("PlatformNAME non associata a nessuna piattaforma");
            })
        ;
    }

    public List<Platform> searchPlatformByName( String name ) {
        return platformRepository.searchByName( name );
    }






}
