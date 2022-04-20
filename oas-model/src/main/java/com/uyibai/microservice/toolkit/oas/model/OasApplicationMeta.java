package com.uyibai.microservice.toolkit.oas.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 应用 OpenApi Data
 */
@Data
@EqualsAndHashCode
public class OasApplicationMeta implements Serializable {
    // service 信息: service 类型(dubbo,rest), service url, 接口信息(dubbo特有)
    /**
     * 应用名字
     */
    private String application;

    /**
     * uri 前缀
     */
    private String uriPrefix;

    /**
     * service list
     */
    private Set<OasServiceMeta> services;

    private long time = 0L;
}
