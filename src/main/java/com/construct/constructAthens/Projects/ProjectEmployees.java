package com.construct.constructAthens.Projects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectEmployees {
    @Column(name = "fullname")
    private String employeeName;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;


}