package com.example.application.data.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String manufacturer;

    private String model;

    private String registrationLicenseNo;

    private String stateRegNo;

    private Integer yearProduced;

    private boolean octa;

    private boolean kasko;

    @Override
    public String toString() {
        return "id=" + id +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", registrationLicenseNo='" + registrationLicenseNo + '\'' +
                ", stateRegNo='" + stateRegNo + '\'' +
                ", yearProduced=" + yearProduced +
                ", octa=" + octa +
                ", kasko=" + kasko;
    }
}
