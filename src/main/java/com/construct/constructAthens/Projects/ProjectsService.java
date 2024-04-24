package com.construct.constructAthens.Projects;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeRepository;
import com.construct.constructAthens.Employees.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectsService {
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
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


    public Projects createProjectWithEmployee(Projects project) {
        project.setProjectId(UUID.randomUUID());
        List<ProjectEmployees> projectEmployeesList = project.getProjectEmployees();
        for (ProjectEmployees projectEmployees : projectEmployeesList) {
            Employee employee = employeeRepository.findEmployeeById(projectEmployees.getEmployeeId());
            if (employee != null) {
                ProjectsEmployee projectsEmployee = new ProjectsEmployee();
                projectsEmployee.setNameProject(project.getNameProject());
                projectsEmployee.setStatusProject(project.getStatusProject());

                List<String> headOfSiteIds = new ArrayList<>();
                for (ProjectHeadSite headSite : project.getProjectHeadSites()) {
                    headOfSiteIds.add(headSite.getHeadSiteId().toString());
                }
                String headOfSiteJson = String.join(", ", headOfSiteIds);

                projectsEmployee.setHeadOfSite(headOfSiteJson);
                projectsEmployee.setMyContribution(new ProjectsEmployee.MyContribution(projectEmployees.getStartDate(), projectEmployees.getEndDate()));

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