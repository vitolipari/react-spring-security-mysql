package com.liparistudios.reactspringsecmysql.service;

import com.liparistudios.reactspringsecmysql.model.Platform;
import com.liparistudios.reactspringsecmysql.model.Session;
import com.liparistudios.reactspringsecmysql.repository.SessionRepository;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired private PlatformService platformService;

    /*
    List<Session> findByOpenBefore(LocalDateTime open );
    List<Session> findByOpenAfter( LocalDateTime open );
    List<Session> findAllClosedSessions();
    List<Session> findAllByCustomerId( Long customerId );

     * TODO ----------------------------------------------------
    List<Session> findAllExpiredSessions( LocalDateTime open );

     * TODO ----------------------------------------------------
    List<Session> findAllNotExpiredSessions( LocalDateTime open );


     * TODO ----------------------------------------------------
    List<Session> findAllExpiredNotClosedSessions( LocalDateTime open );

     */


    public Session save( Session session ) {
        return sessionRepository.save( session );
    }

    public Session openNewSession( Long platformId ) {

        Long now = System.currentTimeMillis();
        StringBuilder result = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte [] digested = md.digest((Long.toString( now )).getBytes());
            for (int i = 0; i < digested.length; i++) {
                result.append(Integer.toHexString(0xFF & digested[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Bad algorithm");
        }
        @Length(min = 64, max = 64)
        String code = result.toString();;


        LocalDateTime open = LocalDateTime.now();

        // da controllare
        Period startUpTime = Period.between(LocalDate.now(), LocalDate.now().plus(90, ChronoUnit.SECONDS) );

        LocalDateTime expies = open.plus(90, ChronoUnit.DAYS);

        Session newSession =
            new Session(
                null,
                code,
                open,
                null,
                null,
                expies,
                null,
                null
            )
        ;

        Platform platform = platformService.getPlatformById( platformId );
        List<Session> platformSessions = platform.getSessions();
        platformSessions.add(newSession);
        platform.setSessions( platformSessions );
        platformService.save( platform );

        return sessionRepository.save( newSession );
    }


    public Session getSessionByCode( String code ) {
        return
            sessionRepository
                .findByCode( code )
                .orElseThrow( () -> {
                    throw new IllegalStateException("SessionID non associata a nessuna sessione");
                })
        ;
    }



}
