package com.construct.constructAthens.Projects;


import com.construct.constructAthens.Employees.Employee_dependencies.ProjectsEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectsService {
    private final ProjectsRepository projectsRepository;

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
}