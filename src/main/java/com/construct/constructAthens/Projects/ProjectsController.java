package com.construct.constructAthens.Projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
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
    @GetMapping("/employees-or-headsites/{id}/projects")
    public ResponseEntity<List<ProjectDetails>> getProjectsByEmployeeOrHeadSiteId(@PathVariable("id") UUID id) {
        List<ProjectDetails> projectDetails = projectsService.getProjectsByEmployeeOrHeadSiteId(id);
        return ResponseEntity.ok(projectDetails);
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

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody Projects project) {
        try {
            Projects createdProject = projectsService.createProjectWithEmployee(project);
            return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID projectId) {
        projectsService.deleteProject(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

/*    @PatchMapping("/{projectId}")
    public ResponseEntity<Projects> updateProjectByFields(@PathVariable("projectId") UUID projectId,
                                                          @RequestBody Map<String, Object> fields) {
        try {
            Projects updatedProject = projectsService.updateProjectByFields(projectId, fields);
            if (updatedProject != null) {
                return ResponseEntity.ok(updatedProject);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }*/
    @PutMapping("/{projectId}")
    public ResponseEntity<Projects> editProject(@PathVariable UUID projectId, @RequestBody Projects newProjectData) {
        Projects updatedProject = projectsService.editProject(projectId, newProjectData);
        return ResponseEntity.ok(updatedProject);
    }
}