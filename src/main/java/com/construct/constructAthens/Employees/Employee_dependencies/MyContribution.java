package com.construct.constructAthens.Employees.Employee_dependencies;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public  class MyContribution {
    private String startDataContribution;
    private String endDataContribution;

    public MyContribution(String startDataContribution, String endDataContribution) {
        this.startDataContribution = startDataContribution;
        this.endDataContribution = endDataContribution;
    }

    public MyContribution() {
    }

    public String getStartDataContribution() {
        return startDataContribution;
    }

    public void setStartDataContribution(String startDataContribution) {
        this.startDataContribution = startDataContribution;
    }

    public String getEndDataContribution() {
        return endDataContribution;
    }

    public void setEndDataContribution(String endDataContribution) {
        this.endDataContribution = endDataContribution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyContribution that = (MyContribution) o;
        return Objects.equals(startDataContribution, that.startDataContribution) &&
                Objects.equals(endDataContribution, that.endDataContribution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDataContribution, endDataContribution);
    }
}
