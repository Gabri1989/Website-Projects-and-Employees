package com.construct.constructAthens.Employees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.construct.constructAthens.AzureStorage.StorageService;
import com.construct.constructAthens.Employees.Employee_dependencies.ForeignLanguage;
import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import com.construct.constructAthens.Employees.Employee_dependencies.Skill;
import com.construct.constructAthens.Employees.Employee_dependencies.WeekSchedule;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service

public class EmployeeService {

    private final EmployeeRepository employeeRepository ;
    @Value("${spring.cloud.azure.storage.blob.connection-string}")
    private String azureStorageConnectionString;
    @Autowired
    private StorageService azureBlobAdapter;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    private BlobContainerClient blobContainerClient;
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
                    case "imageURL":
                        List<String> blobNames = listBlobs();
                        List<String> sortedBlobNames = blobNames.stream()
                                .sorted(Comparator.comparing(this::getLastModifiedTimestamp).reversed())
                                .toList();

                        String lastBlobName = "";
                        if (!sortedBlobNames.isEmpty()) {
                            lastBlobName = sortedBlobNames.get(0);
                            lastBlobName = lastBlobName.substring(lastBlobName.lastIndexOf('/') + 1);
                        }
                        String imageURL = "https://ipstorage1989.blob.core.windows.net/atenacontainer/" + lastBlobName;
                        existingEmployee.get().setImageURL(imageURL);
                        break;
                    default:
                        handleEmployeeField(existingEmployee.get(), key, value);
                }
            });
            return employeeRepository.save(existingEmployee.get());
        }
        return null;
    }
    private List<String> listBlobs() {
        return blobContainerClient.listBlobs().stream()
                .map(BlobItem::getName)
                .collect(Collectors.toList());
    }
    private long getLastModifiedTimestamp(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        BlobProperties properties = blobClient.getProperties();
        OffsetDateTime lastModified = properties.getLastModified();
        return lastModified.toInstant().toEpochMilli();
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
                // Update existing project fields
                updateProjectsEmployee(existingProject.get(), updatedProject);
            } else {
                // Create new project
                ProjectsEmployee newProject = new ProjectsEmployee();
                updateProjectsEmployee(newProject, updatedProject);
                // Add new project to employee
                employee.getProjects().add(newProject);
            }
        }
    }

    private void updateProjectsEmployee(ProjectsEmployee project, Map<String, Object> updatedProject) {
        // Fetch existing values
        String nameProject = project.getNameProject();
        String statusProject = project.getStatusProject(); // Changed type to String
        int timpPerDate = project.getTimpPerDate();
        String role = project.getRole();
        String headOfSite = project.getHeadOfSite();
        ProjectsEmployee.MyContribution myContribution = project.getMyContribution();

        // Update only the fields provided in the updatedProject map
        if (updatedProject.containsKey("nameProject")) {
            project.setNameProject((String) updatedProject.get("nameProject"));
        }
        if (updatedProject.containsKey("statusProject")) {
            project.setStatusProject((String) updatedProject.get("statusProject")); // Changed type to String
        }
        if (updatedProject.containsKey("timpPerDate")) {
            project.setTimpPerDate((int) updatedProject.get("timpPerDate"));
        }
        if (updatedProject.containsKey("role")) {
            project.setRole((String) updatedProject.get("role"));
        }
        if (updatedProject.containsKey("headOfSite")) {
            project.setHeadOfSite((String) updatedProject.get("headOfSite"));
        }
        // MyContribution handling
        if (updatedProject.containsKey("myContribution")) {
            Map<String, Object> myContributionMap = (Map<String, Object>) updatedProject.get("myContribution");
            ProjectsEmployee.MyContribution updatedMyContribution = new ProjectsEmployee.MyContribution();
            if (myContributionMap.containsKey("startDataContribution")) {
                String startDataContributionStr = (String) myContributionMap.get("startDataContribution");
                LocalDate startDataContribution = LocalDate.parse(startDataContributionStr);
                updatedMyContribution.setStartDataContribution(startDataContribution);
            } else {
                updatedMyContribution.setStartDataContribution(myContribution.getStartDataContribution());
            }
            if (myContributionMap.containsKey("endDataContribution")) {
                String endDataContributionStr = (String) myContributionMap.get("endDataContribution");
                LocalDate endDataContribution = LocalDate.parse(endDataContributionStr);
                updatedMyContribution.setEndDataContribution(endDataContribution);
            } else {
                updatedMyContribution.setEndDataContribution(myContribution.getEndDataContribution());
            }
            project.setMyContribution(updatedMyContribution);
        }
        // Restore existing values if not provided in the update request
        if (project.getNameProject() == null) {
            project.setNameProject(nameProject);
        }
        if (project.getStatusProject() == null) {
            project.setStatusProject(statusProject);
        }
        if (project.getTimpPerDate() == 0) {
            project.setTimpPerDate(timpPerDate);
        }
        if (project.getRole() == null) {
            project.setRole(role);
        }
        if (project.getHeadOfSite() == null) {
            project.setHeadOfSite(headOfSite);
        }
        // Restore MyContribution if not provided in the update request
        if (project.getMyContribution() == null) {
            project.setMyContribution(myContribution);
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
            DayOfWeek day = DayOfWeek.valueOf(dayString.toUpperCase()); // Parse day string to DayOfWeek

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
                e.printStackTrace();
            }
        }
    }


    /*public Employee updateEmployeeByFields(UUID id, Map<String, Object> fields) {
        Optional<Employee> existingEmployee = employeeRepository.findById(id);

        if (existingEmployee.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Employee.class, key);
                if (field != null) {
                    field.setAccessible(true);
                    try {
                        if (field.getType() == LocalDate.class && value instanceof String) {
                            LocalDate dateValue = LocalDate.parse((String) value);
                            field.set(existingEmployee.get(), dateValue);
                        } else {
                            field.set(existingEmployee.get(), value);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
            return employeeRepository.save(existingEmployee.get());
        }
        return null;
    }*/
}