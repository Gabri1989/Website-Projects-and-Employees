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
            Employee employee = employeeRepository.findEmployeeByFullname(projectEmployees.getEmployeeName());
            if (employee != null) {
                ProjectsEmployee projectsEmployee = new ProjectsEmployee();
                projectsEmployee.setNameProject(project.getNameProject());
                projectsEmployee.setStatusProject(project.getStatusProject());

                List<String> headOfSiteNames = new ArrayList<>();
                for (ProjectHeadSite headSite : project.getProjectHeadSites()) {
                    headOfSiteNames.add(headSite.getFullName());
                }
                String headOfSiteJson = String.join(", ", headOfSiteNames);
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
            fields.forEach((key, value) -> {
                handleProjectField(existingProject, key, value);
            });

         /*   // Propagate changes to associated tables
            List<ProjectEmployees> projectEmployees = existingProject.getProjectEmployees();
            for (ProjectEmployees employee : projectEmployees) {
                // Update project name in embedded ProjectsEmployee
                if (employee.getNameProject().equals(projectName)) {
                    employee.setNameProject(existingProject.getNameProject());
                    // Update other fields if needed
                }
            }*/

            // Update project name in embedded ProjectsEmployee within Employee
            List<Employee> employees = employeeRepository.findAll();
            for (Employee employee : employees) {
                for (ProjectsEmployee employeeProject : employee.getProjects()) {
                    if (employeeProject.getNameProject().equals(projectName)) {
                        employeeProject.setNameProject(existingProject.getNameProject());
                        // Update other fields if needed
                    }
                }
                employeeRepository.save(employee);
            }

            // Update project name in embedded ProjectHeadSite
            List<ProjectHeadSite> projectHeadSites = existingProject.getProjectHeadSites();
            for (ProjectHeadSite headSite : projectHeadSites) {
                headSite.setFullName(existingProject.getNameProject());
                // Update other fields if needed
            }

            // Save changes
            projectsRepository.save(existingProject);
            return existingProject;
        }

        return null;
    }


  /*  public Projects updateProjectByFields(UUID projectId, Map<String, Object> fields) {
        Optional<Projects> existingProject = projectsRepository.findById(projectId);

        if (existingProject.isPresent()) {
            fields.forEach((key, value) -> {
                handleProjectField(existingProject.get(), key, value);
            });

            return projectsRepository.save(existingProject.get());
        }

        return null;
    }*/


}