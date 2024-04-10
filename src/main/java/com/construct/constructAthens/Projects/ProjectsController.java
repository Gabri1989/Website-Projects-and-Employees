package com.construct.constructAthens.Projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectsController {
    private final ProjectsService projectsService;

    @Autowired
    public ProjectsController(ProjectsService projectsService) {
        this.projectsService = projectsService;
    }

    @GetMapping
    public ResponseEntity<List<Projects>> getAllProjects() {
        List<Projects> projects = projectsService.getAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Projects> getProjectById(@PathVariable UUID projectId) {
        Projects project = projectsService.getProjectById(projectId);
        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Projects> createProject(@RequestBody Projects project) {
        Projects savedProject = projectsService.saveProject(project);
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        projectsService.deleteProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PatchMapping("/projects/{projectId}")
    public ResponseEntity<Projects> updateProjectByFields(@PathVariable("projectId") UUID projectId, @RequestBody Map<String, Object> fields) {
        Projects updatedProject = projectsService.updateProjectByFields(projectId, fields);

        if (updatedProject != null) {
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}