package com.uyibai.microservice.toolkit.oas;


import java.util.Set;

public interface DubboOasService {

    /**
     * Current version of the interface contract.
     */
    String VERSION = "1.0.0";

    /**
     * Get Dubbo Service OpenApi Info
     *
     * @param interfaceName dubbo Service Interface name
     * @return OpenApi Json String
     */
    String getServiceOasMeta(String interfaceName);

    /**
     * Get current Microservice all OpenApi Info
     *
     * @return
     */
    String getApplicationOasMeta();

    /**
     * Get All Dubbo Service Interface
     *
     * @return
     */
    Set<String> getAllService();

}
