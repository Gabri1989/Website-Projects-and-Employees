package com.construct.constructAthens.Projects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectHeadSite {
    @Column(name = "headsite_id")
    private UUID headSiteId;
    @JsonIgnore
    @Column(name = "start_date")
    private String startDate;
    @JsonIgnore
    @Column(name = "end_date")
    private String endDate;

}