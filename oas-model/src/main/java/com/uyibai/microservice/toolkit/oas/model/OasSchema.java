package com.uyibai.microservice.toolkit.oas.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class OasSchema<T> implements Serializable {
    protected T _default;
    private String name;
    private List<String> required = null;
    private String type = null;
    private Map<String, OasSchema> properties = null;
    private String description = null;
    private String format = null;
}
