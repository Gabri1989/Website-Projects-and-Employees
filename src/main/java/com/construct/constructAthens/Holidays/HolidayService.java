package com.construct.constructAthens.Holidays;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.Employee_dependencies.HolidayWithEmployeeDetailsDTO;
import com.construct.constructAthens.Employees.exception.NotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HolidayService {
    private final HolidayRepository holidayRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
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

    public List<HolidayWithEmployeeDetailsDTO> findAvailableEmployee(LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findAvailableEmployeesWithDetails(startDate, endDate);
    }

  public Integer calculateMaxPeriod(LocalDate startDate, LocalDate endDate) {
      long weeks = ChronoUnit.WEEKS.between(startDate, endDate);
      return Math.toIntExact(weeks)+1;
  }

public List<Holiday> findEmployeesHolidays(LocalDate queryStartDate, LocalDate queryEndDate) {
    // Fetch all employees
    List<Employee> employees = employeeRepository.findAll();

    List<Holiday> holidays = holidayRepository.findHolidaysByEmployees(queryStartDate, queryEndDate);

    // Group holidays by employee ID
    Map<UUID, List<Holiday>> groupedHolidays = holidays.stream().collect(Collectors.groupingBy(Holiday::getEmployeeId));

    List<Holiday> aggregatedHolidays = new ArrayList<>();

    for (Employee employee : employees) {
        List<Holiday> employeeHolidays = groupedHolidays.getOrDefault(employee.getId(), Collections.emptyList());
        if (employeeHolidays.isEmpty()) {
            // If the employee has no holiday, create a new Holiday object with index set to 0
            Holiday holiday = new Holiday();
            holiday.setEmployeeId(employee.getId());
            holiday.setIndex(0);
            aggregatedHolidays.add(holiday);
        } else {
            // Merge overlapping or consecutive holidays
            List<Holiday> mergedHolidays = mergeHolidays(employeeHolidays);

            for (Holiday holiday : mergedHolidays) {
                if ("Pending".equals(holiday.getStatus())) {
                    holiday.setIndex(0);
                } else {
                    Integer index = calculateIndex(holiday.getStartDate(), holiday.getEndDate(), queryStartDate, queryEndDate);
                    holiday.setIndex(index);
                }
                aggregatedHolidays.add(holiday);
            }
        }
    }
    return aggregatedHolidays;
}
    private List<Holiday> mergeHolidays(List<Holiday> holidays) {
        holidays.sort(Comparator.comparing(Holiday::getStartDate));
        List<Holiday> mergedHolidays = new ArrayList<>();

        Holiday current = null;
        for (Holiday holiday : holidays) {
            if (current == null) {
                current = holiday;
            } else {
                if (!holiday.getStartDate().isAfter(current.getEndDate().plusDays(1))) {
                    // Extend the current holiday period
                    current.setEndDate(holiday.getEndDate().isAfter(current.getEndDate()) ? holiday.getEndDate() : current.getEndDate());
                } else {
                    // Add the current holiday period to the list and start a new one
                    mergedHolidays.add(current);
                    current = holiday;
                }
            }
        }
        if (current != null) {
            mergedHolidays.add(current);
        }

        return mergedHolidays;
    }

  public Integer calculateIndex(LocalDate startDate, LocalDate endDate, LocalDate queryStartDate, LocalDate queryEndDate) {
      long totalDays = ChronoUnit.DAYS.between(queryStartDate, queryEndDate) + 1;
      int numberOfWeeks = (int) Math.ceil((double) totalDays / 7);

      // Define the boundaries for each week
      LocalDate[] weekEndDates = new LocalDate[numberOfWeeks];
      for (int i = 0; i < numberOfWeeks; i++) {
          weekEndDates[i] = queryStartDate.plusWeeks(i + 1).minusDays(1);
      }
      weekEndDates[numberOfWeeks - 1] = queryEndDate; // Ensure the last week end date is the query end date

      // Calculate which weeks the holiday overlaps with
      StringBuilder indexBuilder = new StringBuilder();
      for (int i = 0; i < numberOfWeeks; i++) {
          LocalDate weekStartDate = (i == 0) ? queryStartDate : weekEndDates[i - 1].plusDays(1);
          LocalDate weekEndDate = weekEndDates[i];

          if ((startDate.isBefore(weekEndDate) || startDate.equals(weekEndDate)) && endDate.isAfter(weekStartDate.minusDays(1))) {
              indexBuilder.append(i + 1);
          }
      }
      return indexBuilder.length() > 0 ? Integer.parseInt(indexBuilder.toString()) : null;
  }
}