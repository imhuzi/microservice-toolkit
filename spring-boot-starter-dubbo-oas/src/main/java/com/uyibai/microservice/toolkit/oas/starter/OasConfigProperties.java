package com.uyibai.microservice.toolkit.oas.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("microservice")
@Data
public class OasConfigProperties {
    /**
     * microservice uri prefix
     */
    private String uriPrefix = "";

}
