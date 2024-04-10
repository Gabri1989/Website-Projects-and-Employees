package com.construct.constructAthens.Projects;

import com.construct.constructAthens.Employees.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectsService {
    private final ProjectsRepository projectsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProjectsService.class);
    @Autowired
    public ProjectsService(ProjectsRepository projectsRepository) {
        this.projectsRepository = projectsRepository;
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
    }

    public Projects getProjectById(UUID projectId) {
        return projectsRepository.findById(projectId).orElse(null);
    }

    public Projects saveProject(Projects project) {
        return projectsRepository.save(project);
    }
/*    public void createProject(Projects project, UUID employeeUuid) {
        ProjectsEmployee projectsEmployee = projectsRepository.findByEmployeeId(employeeUuid, employeeRepository);
        if (projectsEmployee != null) {
            // Set the project details from ProjectsEmployee
            project.setNameProject(projectsEmployee.getNameProject());
            project.setStatusProject(projectsEmployee.getStatusProject());
            project.setHeadOfSite(projectsEmployee.getHeadOfSite());
            // Save the project
            projectsRepository.save(project);
        } else {
            // Handle case where the employee is not found or is not a ProjectsEmployee
            // Example: throw an exception, log an error, etc.
            // Here, I'll just print a message for demonstration
            System.out.println("ProjectsEmployee not found for employee UUID: " + employeeUuid);
        }
    }*/

    public void deleteProject(UUID projectId) {
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
        Optional<Projects> existingProject = projectsRepository.findById(projectId);

        if (existingProject.isPresent()) {
            fields.forEach((key, value) -> {
                handleProjectField(existingProject.get(), key, value);
            });

            return projectsRepository.save(existingProject.get());
        }

        return null;
    }
}