package com.uyibai.microservice.toolkit.oas.starter;

import com.uyibai.microservice.toolkit.oas.DubboOasService;
import com.uyibai.microservice.toolkit.oas.model.OasApplicationMeta;
import io.swagger.v3.core.util.Json;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class DubboOasServiceImpl implements DubboOasService {

    @Resource
    DubboOasCollector dubboOasCollector;

    /**
     * Get Dubbo Service OpenApi Info
     *
     * @param interfaceName dubbo Service Interface name
     * @return OpenApi Json String
     */
    @SneakyThrows
    @Override
    public String getServiceOasMeta(String interfaceName) {
        return Json.mapper().writeValueAsString(DubboOasDataCache.getOpenApi(interfaceName));
    }

    /**
     * Get current Microservice all OpenApi Info
     *
     * @return
     */
    @SneakyThrows
    @Override
    public String getApplicationOasMeta() {
        OasApplicationMeta applicationMeta = dubboOasCollector.getOasApplicationMeta();
        applicationMeta.setServices(DubboOasDataCache.getAllOpenApi());
        return Json.mapper().writeValueAsString(applicationMeta);
    }

    /**
     * Get All Dubbo Service Interface
     *
     * @return
     */
    @Override
    public Set<String> getAllService() {
        return DubboOasDataCache.getAllKey();
    }
}
