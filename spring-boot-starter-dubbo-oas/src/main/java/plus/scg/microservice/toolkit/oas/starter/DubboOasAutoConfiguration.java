package plus.scg.microservice.toolkit.oas.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

@Configuration(proxyBeanMethods = false)
@Import({OasConfigProperties.class, DubboOasServiceImpl.class, DubboOasCollector.class, DubboOasServiceExporter.class})
public class DubboOasAutoConfiguration {

    @Autowired
    private DubboOasServiceExporter dubboOasServiceExporter;

    @Autowired
    private DubboOasCollector dubboOasCollect;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) throws Exception {
        // Collect OpenApi Data
        dubboOasCollect.collectOpenApiData();
        // export as Dubbo service.
        dubboOasServiceExporter.export();
    }

    @EventListener(ApplicationEnvironmentPreparedEvent.class)
    public void onEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        System.setProperty("generic.include.class", "false");
    }


//    @EventListener(ServiceBeanExportedEvent.class)
//    public void onServiceBeanExported(ServiceBeanExportedEvent event) {
//        ServiceBean serviceBean = event.getServiceBean();
//        dubboOasCollect.collectOpenApiData(serviceBean);
//    }

    @EventListener(ApplicationFailedEvent.class)
    public void onApplicationFailed() {
        unExportDubboMetadataConfigService();
    }

    @EventListener(ContextClosedEvent.class)
    public void onContextClosed() {
        unExportDubboMetadataConfigService();
    }

    private void unExportDubboMetadataConfigService() {
        dubboOasServiceExporter.unexport();
    }

}
