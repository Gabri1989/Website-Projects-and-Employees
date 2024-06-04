package com.construct.constructAthens.Employees;
import com.construct.constructAthens.Employees.Employee_dependencies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service

public class EmployeeService {
    private final EmployeeRepository employeeRepository ;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository)        {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(UUID id)  {
        return employeeRepository.findById(id);
    }
    public Employee getEmployeeByUsername(String username){
        return employeeRepository.findEmployeeByUsername(username);
    }
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }

    public Collection<Skill> getSkillsByEmployeeId(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        return employee.getSkills();
    }


    public Employee updateEmployeeByFields(UUID id, Map<String, Object> fields) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);

        if (existingEmployee.isPresent()) {
            fields.forEach((key, value) -> {
                switch (key) {
                    case "foreignLanguages":
                        updateForeignLanguages(existingEmployee.get(), (List<Map<String, String>>) value);
                        break;
                    case "projects":
                        updateProjects(existingEmployee.get(), (List<Map<String, Object>>) value);
                        break;
                    case "skills":
                        updateSkills(existingEmployee.get(), (List<Map<String, String>>) value);
                        break;
                    case "weekSchedules":
                        updateWeekSchedules(existingEmployee.get(), (List<Map<String, String>>) value);
                        break;
                    default:
                        handleEmployeeField(existingEmployee.get(), key, value);
                }
            });
            return employeeRepository.save(existingEmployee.get());
        }
        return null;
    }

    private void updateForeignLanguages(Employee employee, List<Map<String, String>> updatedLanguages) {
        for (Map<String, String> updatedLanguage : updatedLanguages) {
            String languageName = updatedLanguage.get("name");
            String languageLevel = updatedLanguage.get("level");
            Optional<ForeignLanguage> existingLanguage = employee.getForeignLanguages().stream()
                    .filter(language -> language.getName().equals(languageName))
                    .findFirst();
            if (existingLanguage.isPresent()) {
                existingLanguage.get().setLevel(languageLevel);
            } else {
                ForeignLanguage foreignLanguage = new ForeignLanguage();
                foreignLanguage.setName(languageName);
                foreignLanguage.setLevel(languageLevel);
                employee.getForeignLanguages().add(foreignLanguage);
            }
        }
    }

    private void updateProjects(Employee employee, List<Map<String, Object>> updatedProjects) {
        for (Map<String, Object> updatedProject : updatedProjects) {
            String projectName = (String) updatedProject.get("nameProject");
            Optional<ProjectsEmployee> existingProject = employee.getProjects().stream()
                    .filter(project -> project.getNameProject().equals(projectName))
                    .findFirst();
            if (existingProject.isPresent()) {
                updateProjectsEmployee(existingProject.get(), updatedProject);
            } else {
                ProjectsEmployee newProject = new ProjectsEmployee();
                updateProjectsEmployee(newProject, updatedProject);
                employee.getProjects().add(newProject);
            }
        }
    }

    private void updateProjectsEmployee(ProjectsEmployee project, Map<String, Object> updatedProject) {
        double timpPerDate = project.getTimpPerDate();
        String role = project.getRole();

        if (updatedProject.containsKey("timpPerDate")) {
            project.setTimpPerDate((int) updatedProject.get("timpPerDate"));
        }
        if (updatedProject.containsKey("role")) {
            project.setRole((String) updatedProject.get("role"));
        }
        if (project.getTimpPerDate() == 0) {
            project.setTimpPerDate(timpPerDate);
        }
        if (project.getRole() == null) {
            project.setRole(role);
        }
    }

    private void updateSkills(Employee employee, List<Map<String, String>> updatedSkills) {
        for (Map<String, String> updatedSkill : updatedSkills) {
            String skillName = updatedSkill.get("skillName");
            Optional<Skill> existingSkill = employee.getSkills().stream()
                    .filter(skill -> skill.getSkillName().equals(skillName))
                    .findFirst();
            if (existingSkill.isPresent()) {
                existingSkill.get().setExperience(updatedSkill.get("experience"));
                existingSkill.get().setLevel(updatedSkill.get("level"));
            } else {
                Skill skill = new Skill();
                skill.setSkillName(skillName);
                skill.setExperience(updatedSkill.get("experience"));
                skill.setLevel(updatedSkill.get("level"));
                employee.getSkills().add(skill);
            }
        }
    }


    private void updateWeekSchedules(Employee employee, List<Map<String, String>> updatedSchedules) {
        for (Map<String, String> updatedSchedule : updatedSchedules) {
            String dayString = updatedSchedule.get("day");
            DayOfWeek day = DayOfWeek.valueOf(dayString.toUpperCase());

            String startSchedule = updatedSchedule.get("startSchedule");
            String endSchedule = updatedSchedule.get("endSchedule");

            Optional<WeekSchedule> existingSchedule = employee.getWeekSchedules().stream()
                    .filter(schedule -> schedule.getDay() == day)
                    .findFirst();
            if (existingSchedule.isPresent()) {
                existingSchedule.get().setStartSchedule(startSchedule);
                existingSchedule.get().setEndSchedule(endSchedule);
            } else {
                WeekSchedule newSchedule = new WeekSchedule();
                newSchedule.setDay(day);
                newSchedule.setStartSchedule(startSchedule);
                newSchedule.setEndSchedule(endSchedule);
                employee.getWeekSchedules().add(newSchedule);
            }
        }
    }

    private void handleEmployeeField(Employee employee, String key, Object value) {
        Field field = ReflectionUtils.findField(Employee.class, key);
        if (field != null) {
            field.setAccessible(true);
            try {
                if (field.getType() == LocalDate.class && value instanceof String) {
                    LocalDate dateValue = LocalDate.parse((String) value);
                    field.set(employee, dateValue);
                } else {
                    field.set(employee, value);
                }
            } catch (IllegalAccessException e) {
                logger.error("An error occurred:", e);
            }
        }
    }
    public Employee updateSkills(UUID id, List<Map<String, String>> skills) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        if (existingEmployee.isPresent()) {
            Employee employee = existingEmployee.get();
            // Clear existing skills and add the updated list
            employee.getSkills().clear();
            for (Map<String, String> updatedSkill : skills) {
                Skill skill = new Skill();
                skill.setSkillName(updatedSkill.get("skillName"));
                skill.setExperience(updatedSkill.get("experience"));
                skill.setLevel(updatedSkill.get("level"));
                employee.getSkills().add(skill);
            }
            return employeeRepository.save(employee);
        }
        return null;
    }

    public Employee updateWeekSchedules(UUID id, List<Map<String, String>> schedules) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        if (existingEmployee.isPresent()) {
            Employee employee = existingEmployee.get();
            // Clear existing week schedules and add the updated list
            employee.getWeekSchedules().clear();
            for (Map<String, String> updatedSchedule : schedules) {
                WeekSchedule schedule = new WeekSchedule();
                schedule.setDay(DayOfWeek.valueOf(updatedSchedule.get("day").toUpperCase()));
                schedule.setStartSchedule(updatedSchedule.get("startSchedule"));
                schedule.setEndSchedule(updatedSchedule.get("endSchedule"));
                employee.getWeekSchedules().add(schedule);
            }
            return employeeRepository.save(employee);
        }
        return null;
    }


    public Employee updateForeignLanguages(UUID id, List<Map<String, String>> languages) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);
        if (existingEmployee.isPresent()) {
            Employee employee = existingEmployee.get();
            // Clear existing foreign languages and add the updated list
            employee.getForeignLanguages().clear();
            for (Map<String, String> updatedLanguage : languages) {
                ForeignLanguage language = new ForeignLanguage();
                language.setName(updatedLanguage.get("name"));
                language.setLevel(updatedLanguage.get("level"));
                employee.getForeignLanguages().add(language);
            }
            return employeeRepository.save(employee);
        }
        return null;
    }


}