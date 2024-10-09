package com.lhqjlb.project.entity;

import lombok.Data;

import java.util.List;

@Data
public class Options {

    private Long value;
    private String label;
    private List<Options> children;
}
