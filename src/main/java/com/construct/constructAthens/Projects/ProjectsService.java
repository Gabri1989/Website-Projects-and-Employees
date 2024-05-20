package com.construct.constructAthens.Projects;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.EmployeeService;
import com.construct.constructAthens.Employees.Employee_dependencies.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
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
        List<Projects> projects = projectsRepository.findAll().stream()
                .filter(project -> project.getProjectEmployees().stream()
                        .anyMatch(projectEmployee -> projectEmployee.getEmployeeId().equals(id)) ||
                        project.getProjectHeadSites().stream()
                                .anyMatch(projectHeadSite -> projectHeadSite.getHeadSiteId().equals(id)))
                .collect(Collectors.toList());

        return projects.stream()
                .map(project -> {
                    List<EmployeeCheckInOut> employeeTimes = employeeTimeGpsRepository.findByEmployeeIdAndProjectId(id, project.getProjectId()).stream()
                            .map(employeeTime -> new EmployeeCheckInOut(employeeTime.getCheckIn(), employeeTime.getCheckOut()))
                            .collect(Collectors.toList());
                    return new ProjectDetails(project, employeeTimes);
                })
                .collect(Collectors.toList());
    }


    public Projects createProjectWithEmployee(Projects project) {
        project.setProjectId(UUID.randomUUID());
        project.setStatusProject("ON_GOING");
        ProjectsEmployee projectsEmployee = new ProjectsEmployee();
        List<ProjectEmployees> projectEmployeesList = project.getProjectEmployees();
        List<ProjectHeadSite> projectHeadSitesList=project.getProjectHeadSites();
        for (ProjectHeadSite projectHeadSite : projectHeadSitesList) {
            Employee employee = employeeRepository.findEmployeeById(projectHeadSite.getHeadSiteId());
            if(employee!=null){
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

        for (Employee employee : employees) {
            employee.getProjects().removeIf(projectsEmployee -> projectsEmployee.getNameProject().equals(project.getNameProject()));
            employeeRepository.save(employee);
        }
        project.setProjectHeadSites(new ArrayList<>());
        project.setProjectEmployees(new ArrayList<>());
        projectsRepository.save(project);
        projectsRepository.deleteById(projectId);
    }

    private void handleProjectField(Projects project, String key, Object value) {
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
            String projectName = existingProject.getNameProject();
            String projectStatus= existingProject.getStatusProject();
            fields.forEach((key, value) -> {
                handleProjectField(existingProject, key, value);
            });

            List<Employee> employees = employeeRepository.findAll();
            for (Employee employee : employees) {
                for (ProjectsEmployee employeeProject : employee.getProjects()) {
                    if (employeeProject.getNameProject().equals(projectName)) {
                        employeeProject.setNameProject(existingProject.getNameProject());
                    } else if (employeeProject.getStatusProject().equals(projectStatus)) {
                        employeeProject.setStatusProject(existingProject.getStatusProject());
                    }
                } //ramane de completat
                employeeRepository.save(employee);
            }

            List<ProjectHeadSite> projectHeadSites = existingProject.getProjectHeadSites();
            for (ProjectHeadSite headSite : projectHeadSites) {

                if (fields.containsKey("startDate")) {
                    headSite.setStartDate((String) fields.get("startDate"));
                }
                if (fields.containsKey("endDate")) {
                    headSite.setEndDate((String) fields.get("endDate"));
                }
                projectsRepository.save(existingProject);
            }
            projectsRepository.save(existingProject);
            return existingProject;
        }

        return null;
    }
}