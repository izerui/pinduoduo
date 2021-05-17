package com.example.pinduoduo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "report")
public class Report {

    @Id
    private String name;
    private Date updateDate = new Date();
    private byte[] bytes;
}
