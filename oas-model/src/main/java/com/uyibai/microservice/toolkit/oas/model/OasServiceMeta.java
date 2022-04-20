package com.uyibai.microservice.toolkit.oas.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * service 信息: service 类型(dubbo,rest), service url, 接口信息(dubbo特有)
 */
@Data
@EqualsAndHashCode
public class OasServiceMeta implements Serializable {

    /**
     * service 名字， 接口 或者类 名
     */
    private String service;

    /**
     * 接口版本
     */
    private String version;

    /**
     * group
     */
    private String group;

    /**
     * service type
     */
    private String type;

    /**
     * uri 前缀
     */
    private String uriPrefix;

    /**
     * 接口 urls
     */
    private List<String> urls;

    /**
     *  方法 信息
     */
    private Set<OasMethod> methods;

    private Map<String, OasSchema> schemas;

}
