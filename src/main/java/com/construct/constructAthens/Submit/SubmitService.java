package com.construct.constructAthens.Submit;

import com.construct.constructAthens.Employees.Employee;
import com.construct.constructAthens.Employees.exception.NotFoundEx;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SubmitService {
    private final SubmitRepository submitRepository;
    @Autowired
    public SubmitService(SubmitRepository submitRepository) {
        this.submitRepository = submitRepository;
    }
    public List<Submit> gettAllSubmits(){
        return submitRepository.findAll();
    }
    public Optional<Submit> getSubmitById(Long id){
        return submitRepository.findById(id);
    }
    public Submit saveSubmit(Submit submit){

        return submitRepository.save(submit);
    }
    public void deleteSubmit(Long id){
         submitRepository.deleteById(id);
    }
    public boolean partialUpdate(Long id, String key, String value)
            throws NotFoundEx {
        log.info("Search id={}", id);
        Optional<Submit> optional = submitRepository.findById(id);
        if (optional.isPresent()) {
            Submit submit = optional.get();

            if (key.equalsIgnoreCase("name")) {
                log.info("Updating full name");
                submit.setName(value);
            }
            if (key.equalsIgnoreCase("message")) {
                log.info("Updating message");
                submit.setMessage(value);
            }
            if (key.equalsIgnoreCase("email")) {
                log.info("Updating email");
                submit.setEmail(value);
            }
            if (key.equalsIgnoreCase("phone")) {
                log.info("Updating number");
                submit.setPhone(value);
            }
            if (key.equalsIgnoreCase("company")) {
                log.info("Updating company");
                submit.setCompany(value);
            }
            submitRepository.save(submit);
            return true;
        } else {
            throw new NotFoundEx("RESOURCE_NOT_FOUND");
        }
    }
}
