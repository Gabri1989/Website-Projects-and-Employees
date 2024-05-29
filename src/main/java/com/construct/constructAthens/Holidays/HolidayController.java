package com.construct.constructAthens.Holidays;

import com.construct.constructAthens.Employees.exception.NotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/holidays")
@CrossOrigin(origins = "*")
public class HolidayController {
    private final HolidayService holidayService;

    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping
    public List<Holiday> getAllHolidays() {
        return holidayService.getAllHolidays();
    }

    @PostMapping
    public Holiday createHoliday(@RequestBody Holiday holiday) {
        return holidayService.createHoliday(holiday);
    }

    @PutMapping("/{id}/status")
    public Holiday updateHolidayStatus(@PathVariable UUID id, @RequestParam String status) throws NotFoundEx {
        return holidayService.updateHolidayStatus(id, status);
    }
    @GetMapping("/holidays-approved")
    public List<Holiday> getApprovedHolidaysWithinPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ZoneId zoneId = ZoneId.of("Europe/Athens");
        ZonedDateTime startZonedDateTime = startDate.atStartOfDay(zoneId);
        ZonedDateTime endZonedDateTime = endDate.atStartOfDay(zoneId).plusDays(1).minusSeconds(1); // End of the end date

        LocalDate adjustedStartDate = startZonedDateTime.toLocalDate();
        LocalDate adjustedEndDate = endZonedDateTime.toLocalDate();

        return holidayService.findApprovedHolidaysWithinPeriod(adjustedStartDate, adjustedEndDate);
    }
}