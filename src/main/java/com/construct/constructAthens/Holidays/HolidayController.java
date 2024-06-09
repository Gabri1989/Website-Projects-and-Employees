package com.construct.constructAthens.Holidays;

import com.azure.core.annotation.Get;
import com.construct.constructAthens.Employees.Employee_dependencies.HolidayWithEmployeeDetailsDTO;
import com.construct.constructAthens.Employees.exception.NotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/holidays")
@CrossOrigin(origins = "*")
public class HolidayController {
    private final HolidayService holidayService;
    private final HolidayRepository holidayRepository;

    @Autowired
    public HolidayController(HolidayService holidayService, HolidayRepository holidayRepository) {
        this.holidayService = holidayService;
        this.holidayRepository = holidayRepository;
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


    @GetMapping("/holiday-employees")
    public HolidayResponse getHolidayEmployees(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Holiday> holidays = holidayService.findEmployeesHolidays(startDate, endDate);
        Integer maxIndex = holidayService.calculateMaxPeriod(startDate, endDate);
        HolidayResponse response = new HolidayResponse();
        response.setHolidays(holidays);
        response.setMaxIndex(maxIndex);
        return response;
    }

    @GetMapping("/getHolidayByEmployeeID/{id}")
    public ResponseEntity<List<Holiday>> getHolidayByEmployeeId(@PathVariable UUID id) {
        List<Holiday> holidays = holidayRepository.findListHolidaysByEmployeeId(id);
        if (holidays.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(holidays);
    }
    @DeleteMapping("/deleteHoliday/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable UUID id){
        Optional<Holiday> holiday=holidayRepository.findHolidayByHolidayID(id);
        if(holiday.isPresent()){
            holidayService.deleteHoliday(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else {return new ResponseEntity<>(HttpStatus.NOT_FOUND);}
    }


}