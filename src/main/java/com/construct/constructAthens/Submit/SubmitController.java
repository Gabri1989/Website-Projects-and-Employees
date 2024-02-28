package com.construct.constructAthens.Submit;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.EmployeeDTO;
import com.construct.constructAthens.Employees.exception.NotFoundEx;
import com.construct.constructAthens.Employees.exception.NotYetImplementedEx;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/submits")
@CrossOrigin(origins = "*")
public class SubmitController {
    private final SubmitService submitService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public SubmitController(SubmitService submitService) {
        this.submitService = submitService;
    }
    @GetMapping
    public ResponseEntity<List<Submit>> gettAllSubmits(){
        List<Submit> submits=submitService.gettAllSubmits();
        return new ResponseEntity<>(submits, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Submit> getSubmitById(@PathVariable Long id) {
        Optional<Submit> submits = submitService.getSubmitById(id);
        return submits.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PostMapping("/create")
    public ResponseEntity<Submit> addSubmit(@Valid @RequestBody Submit submit) {
        Submit savedSubmit = submitService.saveSubmit(submit);
        return new ResponseEntity<>(savedSubmit, HttpStatus.CREATED);
    }
    @PutMapping("/edit/{id}")
    public ResponseEntity<Submit> updateSubmit(@PathVariable Long id, @RequestBody Submit updatedSubmit) {
        Optional<Submit> existingSubmit = submitService.getSubmitById(id);
        if (existingSubmit.isPresent()) {
            updatedSubmit.setId(id);
            Submit savedEmployee = submitService.saveSubmit(updatedSubmit);
            return new ResponseEntity<>(savedEmployee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubmit(@PathVariable Long id) {
        Optional<Submit> submit = submitService.getSubmitById(id);
        if (submit.isPresent()) {
            submitService.deleteSubmit(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/patchEdit/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Submit> updateSubmit(@PathVariable Long id, @RequestBody List<SubmitDto> submitDTO) {
        try {
            Optional<Submit> submit = submitService.getSubmitById(id);
            Submit submitPatched = applyPatchToSubmit(submitDTO, submit.orElse(null));
            submitService.saveSubmit(submitPatched);
            return ResponseEntity.ok(submitPatched);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private Submit applyPatchToSubmit(List<SubmitDto> submitDTO, Submit targetSubmit) throws JsonProcessingException {
        ObjectNode targetNode = objectMapper.valueToTree(targetSubmit);

        for (SubmitDto submitD : submitDTO) {
            if ("replace".equals(submitD.getOp())) {
                targetNode.put(submitD.getKey(), submitD.getValue());
            }
        }

        return objectMapper.treeToValue(targetNode, Submit.class);
    }
}
