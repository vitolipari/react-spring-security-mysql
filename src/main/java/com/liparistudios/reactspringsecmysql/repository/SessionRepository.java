package com.liparistudios.reactspringsecmysql.repository;

import com.liparistudios.reactspringsecmysql.model.Customer;
import com.liparistudios.reactspringsecmysql.model.Platform;
import com.liparistudios.reactspringsecmysql.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    /**
     *
     * @param code
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.code = ?1")
    Optional<Session> findByCode( String code );


    /**
     * Tutte le sessioni aperte prima della data indicata
     * @param open
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.open <= ?1")
    List<Session> findByOpenBefore( LocalDateTime open );


    /**
     * Tutte le sessioni aperte dopo la data indicata
     * @param open
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.open >= ?1")
    List<Session> findByOpenAfter( LocalDateTime open );



    /**
     * Tutte le sessioni chiuse
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.closed IS NOT NULL")
    List<Session> findAllClosedSessions();



    /**
     * Tutte le sessioni del customer
     * @param customerId
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.customer_id = ?1")
    List<Session> findAllByCustomerId( Long customerId );



    /**
     * TODO ----------------------------------------------------
     * Tutte le sessioni scadute
     * @param open
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.open >= ?1")
    List<Session> findAllExpiredSessions( LocalDateTime open );



    /**
     * TODO ----------------------------------------------------
     * Tutte le sessioni non scadute
     * @param open
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.open >= ?1")
    List<Session> findAllNotExpiredSessions( LocalDateTime open );



    /**
     * TODO ----------------------------------------------------
     * Tutte le sessioni scadute e non chiuse
     * @param open
     * @return
     */
    @Query("SELECT s FROM Session s WHERE s.open >= ?1")
    List<Session> findAllExpiredNotClosedSessions( LocalDateTime open );







}
