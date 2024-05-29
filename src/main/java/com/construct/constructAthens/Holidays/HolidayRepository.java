package com.construct.constructAthens.Holidays;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {
    @Query("SELECT h FROM Holiday h WHERE h.status = 'Approved' AND h.startDate >= :startDate AND h.endDate <= :endDate")
    List<Holiday> findApprovedHolidaysWithinPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}