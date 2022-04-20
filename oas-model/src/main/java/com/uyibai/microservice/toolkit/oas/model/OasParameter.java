package com.uyibai.microservice.toolkit.oas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode
public class OasParameter {
    /**
     * 参数名称
     */
    private String name;

    /**
     * 入参类型
     */
    private String in;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 参数所在位置
     */
    private int index;

    /**
     * 格式化方式
     */
    private String format;

    /**
     * 引用类型
     */
    private String ref;

    /**
     * 媒体类型
     */
    private String mediaType;

    /**
     * 是否是集合类型: array, list
     */
    private String itemType;

    /**
     * array, list item properties
     */
    private Map<String, String> itemProperties;

    @JsonIgnore
    public Boolean isArray() {
        return type != null && type.endsWith("[]");
    }

    @JsonIgnore
    public Boolean isMap() {
        return type != null && type.equals("java.util.Map");
    }

    @JsonIgnore
    public Boolean isObj() {
        return !isArray() && !type.startsWith("java.");
    }


    @JsonIgnore
    public Boolean isList() {
        return type != null && type.equals("java.util.List");
    }


}
