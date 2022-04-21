package plus.scg.microservice.toolkit.oas.starter;

import plus.scg.microservice.toolkit.oas.DubboOasService;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.util.List;

public class DubboOasServiceExporter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private ObjectProvider<DubboOasService> dubboMetadataService;

    @Autowired
    private ObjectProvider<ProtocolConfig> protocolConfigSupplier;

    @Value("${spring.application.name:${dubbo.application.name:application}}")
    private String currentApplicationName;

    /**
     * The ServiceConfig of DubboOasConfigService to be exported, can be nullable.
     */
    private ServiceConfig<DubboOasService> serviceConfig;

    /**
     * export {@link DubboOasService} as Dubbo service.
     * @return the exported {@link URL URLs}
     */
    public List<URL> export() {

        if (serviceConfig == null || !serviceConfig.isExported()) {

            serviceConfig = new ServiceConfig<>();

            serviceConfig.setInterface(DubboOasService.class);
            // Use DubboApiDocProvider.VERSION as the Dubbo Service version
            serviceConfig.setVersion(DubboOasService.VERSION);
            // Use current Spring application name as the Dubbo Service group
            serviceConfig.setGroup(currentApplicationName);
            serviceConfig.setRef(dubboMetadataService.getIfAvailable());
            serviceConfig.setApplication(applicationConfig);
            serviceConfig.setProtocol(protocolConfigSupplier.getIfAvailable());

            serviceConfig.export();

            if (logger.isInfoEnabled()) {
                logger.info("The Dubbo service[{}] has been exported.",
                        serviceConfig.toString());
            }
        }

        return serviceConfig.getExportedUrls();
    }

    /**
     * unexport {@link DubboOasService}.
     */
    @PreDestroy
    public void unexport() {

        if (serviceConfig == null || serviceConfig.isUnexported()) {
            return;
        }

        serviceConfig.unexport();

        if (logger.isInfoEnabled()) {
            logger.info("The Dubbo service[{}] has been unexported.",
                    serviceConfig.toString());
        }
    }

}
