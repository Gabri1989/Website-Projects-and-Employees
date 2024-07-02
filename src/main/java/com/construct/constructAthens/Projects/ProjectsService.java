package com.construct.constructAthens.Projects;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.EmployeeService;
import com.construct.constructAthens.Employees.Employee_dependencies.*;
import com.construct.constructAthens.Projects.Exceptions.ProjectNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class ProjectsService {
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeTimeGpsRepository employeeTimeGpsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProjectsService.class);

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
    }

    public Projects getProjectById(UUID projectId) {
        return projectsRepository.findById(projectId).orElse(null);
    }

    public Projects saveProject(Projects project) {

        return projectsRepository.save(project);
    }

    public List<ProjectDetails> getProjectsByEmployeeOrHeadSiteId(UUID id) {
        ZoneId zoneId = ZoneId.of("Europe/Athens");
        List<Projects> projects = projectsRepository.findAll().stream()
                .filter(project -> project.getProjectEmployees().stream()
                        .anyMatch(projectEmployee -> projectEmployee.getEmployeeId().equals(id)) ||
                        project.getProjectHeadSites().stream()
                                .anyMatch(projectHeadSite -> projectHeadSite.getHeadSiteId().equals(id)))
                .toList();

        return projects.stream()
                .map(project -> {
                    List<EmployeeTime> allEmployeeTimes = employeeTimeGpsRepository.findByEmployeeIdAndProjectId(id, project.getProjectId());
                    LocalDate today = LocalDate.now(zoneId);

                    List<EmployeeTime> todayEmployeeTimes = allEmployeeTimes.stream()
                            .filter(employeeTime -> employeeTime.getDate().equals(today))
                            .toList();
                    List<EmployeeCheckInOut> employeeCheckInOuts = todayEmployeeTimes.stream()
                            .map(employeeTime -> new EmployeeCheckInOut(employeeTime.getCheckIn(),
                                    employeeTime.getCheckOut() != null ? employeeTime.getCheckOut() : null))
                            .collect(Collectors.toList());

                    if (employeeCheckInOuts.isEmpty()) {
                        return new ProjectDetails(project, List.of());
                    } else {
                        return new ProjectDetails(project, employeeCheckInOuts);
                    }
                })
                .collect(Collectors.toList());
    }


    public Projects createProjectWithEmployee(Projects project) {
        if (projectsRepository.existsByNameProject(project.getNameProject())) {
            throw new IllegalArgumentException("A project with the name " + project.getNameProject() + " already exists.");
        }

        project.setProjectId(UUID.randomUUID());
        project.setStatusProject("ON_GOING");

        Set<ProjectEmployees> projectEmployeesList = project.getProjectEmployees();
        Set<ProjectHeadSite> projectHeadSitesList = project.getProjectHeadSites();

        for (ProjectHeadSite projectHeadSite : projectHeadSitesList) {
            Employee employee = employeeRepository.findEmployeeById(projectHeadSite.getHeadSiteId());
            if (employee != null) {
                ProjectsEmployee projectsEmployee = new ProjectsEmployee();
                projectsEmployee.setNameProject(project.getNameProject());
                projectsEmployee.setMyContribution(new MyContribution(project.getStartData(), project.getEndData()));
                projectsEmployee.setRole("Head Site");
                projectsEmployee.setHeadOfSite(String.valueOf(projectHeadSite.getHeadSiteId()));
                projectsEmployee.setStatusProject("ON_GOING");
                employee.getProjects().add(projectsEmployee);
                employeeRepository.save(employee);
            }
        }

        for (ProjectEmployees projectEmployees : projectEmployeesList) {
            Employee employee = employeeRepository.findEmployeeById(projectEmployees.getEmployeeId());
            if (employee != null) {
                ProjectsEmployee projectsEmployee = new ProjectsEmployee();
                projectsEmployee.setNameProject(project.getNameProject());
                projectEmployees.setStartDate(project.getStartData());
                projectEmployees.setEndDate(project.getEndData());
                List<String> headOfSiteIds = new ArrayList<>();
                for (ProjectHeadSite headSite : project.getProjectHeadSites()) {
                    headOfSiteIds.add(headSite.getHeadSiteId().toString());
                    headSite.setHeadSiteId(headSite.getHeadSiteId());
                    headSite.setStartDate(project.getStartData());
                    headSite.setEndDate(project.getEndData());
                }
                projectsEmployee.setRole("worker");
                projectsEmployee.setStatusProject("ON_GOING");
                String headOfSiteJson = String.join(", ", headOfSiteIds);
                projectsEmployee.setHeadOfSite(headOfSiteJson);
                projectsEmployee.setMyContribution(new MyContribution(project.getStartData(), project.getEndData()));
                employee.getProjects().add(projectsEmployee);
                employeeRepository.save(employee);
            }
        }

        return projectsRepository.saveAndFlush(project);
    }

    public void deleteProject(UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElse(null);
        if (project == null) {
            return;
        }
        List<Employee> employees = employeeRepository.findAll();

        LocalDate currentDate = LocalDate.now();

        for (Employee employee : employees) {
            for (ProjectsEmployee projectsEmployee : employee.getProjects()) {
                if (projectsEmployee.getNameProject().equals(project.getNameProject())) {
                    projectsEmployee.setStatusProject("Finished");
                    if (projectsEmployee.getMyContribution() == null) {
                        projectsEmployee.setMyContribution(new MyContribution());
                    }
                    projectsEmployee.getMyContribution().setEndDataContribution(currentDate.toString());
                    //iterator.remove();
                }
            }
            employeeRepository.save(employee);
        }

        project.setProjectHeadSites(new HashSet<>());
        project.setProjectEmployees(new HashSet<>());
        projectsRepository.save(project);
        projectsRepository.deleteById(projectId);
    }
 /*   private void handleProjectField(Projects project, String key, Object value) {
        Field field = ReflectionUtils.findField(Projects.class, key);
        if (field != null) {
            field.setAccessible(true);
            try {
                if (field.getType() == LocalDate.class && value instanceof String) {
                    LocalDate dateValue = LocalDate.parse((String) value);
                    field.set(project, dateValue);
                } else {
                    field.set(project, value);
                }
            } catch (IllegalAccessException e) {
                logger.error("An error occurred:", e);
            }
        }
    }

    public Projects updateProjectByFields(UUID projectId, Map<String, Object> fields) {
        Optional<Projects> existingProjectOptional = projectsRepository.findById(projectId);

        if (existingProjectOptional.isPresent()) {
            Projects existingProject = existingProjectOptional.get();
            String newProjectName = (String) fields.get("nameProject");

            if (newProjectName != null && !newProjectName.equals(existingProject.getNameProject()) &&
                    projectsRepository.existsByNameProject(newProjectName)) {
                throw new IllegalArgumentException("Project with name " + newProjectName + " already exists.");
            }
            fields.forEach((key, value) -> handleProjectField(existingProject, key, value));
            if (fields.containsKey("projectEmployees")) {
                List<Map<String, Object>> projectEmployeesFields = (List<Map<String, Object>>) fields.get("projectEmployees");
                updateProjectEmployees(existingProject, projectEmployeesFields);
            }
            if (fields.containsKey("projectHeadSites")) {
                List<Map<String, Object>> projectHeadSitesFields = (List<Map<String, Object>>) fields.get("projectHeadSites");
                updateProjectHeadSites(existingProject, projectHeadSitesFields);
            }
            projectsRepository.save(existingProject);
            return existingProject;
        }
        return null;
    }



   private void updateProjectEmployees(Projects project, List<Map<String, Object>> projectEmployeesFields) {
        List<ProjectEmployees> updatedEmployees = new ArrayList<>();

        for (Map<String, Object> empFields : projectEmployeesFields) {
            ProjectEmployees employee = new ProjectEmployees();
            employee.setEmployeeId(UUID.fromString((String) empFields.get("employeeId")));
            updatedEmployees.add(employee);
        }

        project.setProjectEmployees(updatedEmployees);
    }





    private void updateProjectHeadSites(Projects project, List<Map<String, Object>> projectHeadSitesFields) {
        List<ProjectHeadSite> updatedHeadSites = new ArrayList<>();

        for (Map<String, Object> siteFields : projectHeadSitesFields) {
            ProjectHeadSite headSite = new ProjectHeadSite();
            headSite.setHeadSiteId(UUID.fromString((String) siteFields.get("headSiteId")));
            updatedHeadSites.add(headSite);
        }

        project.setProjectHeadSites(updatedHeadSites);
    }
*/
 @Transactional
 public Projects editProject(UUID projectId, Projects newProjectData) {
     Optional<Projects> optionalProject = projectsRepository.findById(projectId);

     if (optionalProject.isPresent()) {
         Projects existingProject = optionalProject.get();

         // Update project details
         String oldNameProject = existingProject.getNameProject();
         String newNameProject = newProjectData.getNameProject();
         existingProject.setNameProject(newNameProject);
         existingProject.setStartData(newProjectData.getStartData());
         existingProject.setEndData(newProjectData.getEndData());
         existingProject.setLatitude(newProjectData.getLatitude());
         existingProject.setLongitude(newProjectData.getLongitude());
         existingProject.setRadius(newProjectData.getRadius());

         Set<UUID> newEmployeeIds = newProjectData.getProjectEmployees().stream()
                 .map(ProjectEmployees::getEmployeeId)
                 .collect(Collectors.toSet());

         existingProject.getProjectEmployees().removeIf(existingEmployee -> {
             boolean isRemoved = !newEmployeeIds.contains(existingEmployee.getEmployeeId());
             if (isRemoved) {
                 updateEmployeeEndDataContribution(existingEmployee.getEmployeeId(), oldNameProject);
             }
             return isRemoved;
         });
         Set<UUID> newHeadSiteIds = newProjectData.getProjectHeadSites().stream()
                 .map(ProjectHeadSite::getHeadSiteId)
                 .collect(Collectors.toSet());

         existingProject.getProjectHeadSites().removeIf(existingHeadSite -> {
             boolean isRemoved = !newHeadSiteIds.contains(existingHeadSite.getHeadSiteId());
             if (isRemoved) {
                 updateEmployeeEndDataContribution(existingHeadSite.getHeadSiteId(), oldNameProject);
             }
             return isRemoved;
         });

         Set<ProjectEmployees> updatedEmployees = new HashSet<>(existingProject.getProjectEmployees());
         updatedEmployees.addAll(newProjectData.getProjectEmployees());
         existingProject.setProjectEmployees(updatedEmployees);

         Set<ProjectHeadSite> updatedHeadSites = new HashSet<>(existingProject.getProjectHeadSites());
         updatedHeadSites.addAll(newProjectData.getProjectHeadSites());
         existingProject.setProjectHeadSites(updatedHeadSites);
         updateEmployeeProjectNames(oldNameProject, newNameProject);

         return projectsRepository.save(existingProject);
     } else {
         throw new ProjectNotFoundException("Project with ID " + projectId + " not found.");
     }
 }

    private void updateEmployeeEndDataContribution(UUID employeeId, String projectName) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            LocalDate currentDate = LocalDate.now();
            for (ProjectsEmployee projectsEmployee : employee.getProjects()) {
                if (projectsEmployee.getNameProject().equals(projectName)) {
                    projectsEmployee.setStatusProject("Finished");
                    if (projectsEmployee.getMyContribution() == null) {
                        projectsEmployee.setMyContribution(new MyContribution());
                    }
                    projectsEmployee.getMyContribution().setEndDataContribution(currentDate.toString());
                }
            }
            employeeRepository.save(employee);
        }
    }

    private void updateEmployeeProjectNames(String oldNameProject, String newNameProject) {
        List<Employee> employees = employeeRepository.findAll();
        for (Employee employee : employees) {
            for (ProjectsEmployee projectsEmployee : employee.getProjects()) {
                if (projectsEmployee.getNameProject().equals(oldNameProject)) {
                    projectsEmployee.setNameProject(newNameProject);
                }
            }
            employeeRepository.save(employee);
        }
    }
}

