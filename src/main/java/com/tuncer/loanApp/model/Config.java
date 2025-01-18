package com.tuncer.loanApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "config")
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int configurationId;
    @Column(name = "configurationkey")
    private String configurationKey;
    @Column(name = "configurationvalue")
    private String configurationValue;
    private String explanation;

}
