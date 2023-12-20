package com.construct.constructAthens.Submit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

}
