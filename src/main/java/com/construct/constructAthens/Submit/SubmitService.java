package com.construct.constructAthens.Submit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        log.info("Gaseste toate cererile..");
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


}
