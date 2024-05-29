package com.construct.constructAthens.Holidays;

import com.construct.constructAthens.Employees.exception.NotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class HolidayService {
    private final HolidayRepository holidayRepository;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public List<Holiday> getAllHolidays() {
        return holidayRepository.findAll();
    }

    public Holiday createHoliday(Holiday holiday) {
        holiday.setStatus("Pending");
        return holidayRepository.save(holiday);
    }

    public Holiday updateHolidayStatus(UUID id, String status) throws NotFoundEx {
        Holiday holiday = holidayRepository.findById(id).orElseThrow(() -> new NotFoundEx("Holiday not found"));
        holiday.setStatus(status);
        return holidayRepository.save(holiday);
    }
    public List<Holiday> findApprovedHolidaysWithinPeriod(LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findApprovedHolidaysWithinPeriod(startDate, endDate);
    }
}