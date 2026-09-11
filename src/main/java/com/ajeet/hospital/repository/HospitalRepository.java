package com.ajeet.hospital.repository;

import com.ajeet.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    @Query("""
        SELECT h
        FROM Hospital h
        WHERE h.active = true
        AND
            (:query IS NULL OR :query = ''
             OR LOWER(h.name) LIKE LOWER(CONCAT('%', :query, '%')))
        AND
            (:location IS NULL OR :location = ''
             OR LOWER(h.location) LIKE LOWER(CONCAT('%', :location, '%')))
        ORDER BY h.name ASC
        """)
    List<Hospital> searchPublicHospitals(
            @Param("query") String query,
            @Param("location") String location
    );

    List<Hospital> findByActiveTrue();

    List<Hospital> findByActiveFalse();

    Optional<Hospital> findByIdAndActiveTrue(Long id);
}