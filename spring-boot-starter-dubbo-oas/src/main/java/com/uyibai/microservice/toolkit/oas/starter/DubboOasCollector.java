package com.uyibai.microservice.toolkit.oas.starter;

import com.uyibai.microservice.toolkit.generator.context.OasGenerator;
import com.uyibai.microservice.toolkit.oas.model.OasApplicationMeta;
import com.uyibai.microservice.toolkit.oas.model.OasMethod;
import com.uyibai.microservice.toolkit.oas.model.OasServiceMeta;
import io.swagger.v3.core.util.Json;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * dubbo openApi 收集器
 * 两种方式：
 * 1.  BeanPostProcessor 实现 接口
 * 2. 订阅 ServiceBeanExportedEvent 事件
 */
@Slf4j
public class DubboOasCollector implements BeanPostProcessor {

    private final ConcurrentMap<String, ServiceBean> dubboBeansCache = new ConcurrentHashMap<>();

    private final OasGenerator oasGenerator = new OasGenerator();

    private final OasApplicationMeta oasApplicationMeta = new OasApplicationMeta();

    @Value("${spring.application.name:${dubbo.application.name:application}}")
    private String currentApplicationName;

    @Autowired
    OasConfigProperties oasConfigProperties;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof ServiceBean)) {
            return null;
        }
        dubboBeansCache.put(beanName, ((ServiceBean) bean));
        return null;
    }

    public void collectOpenApiData() throws Exception {
        String uriPrefix = StringUtils.hasText(oasConfigProperties.getUriPrefix()) ? oasConfigProperties.getUriPrefix() : StringUtils.uncapitalize(currentApplicationName.split("-")[0]);
        oasApplicationMeta.setApplication(currentApplicationName);
        oasApplicationMeta.setUriPrefix(uriPrefix);
        oasApplicationMeta.setTime(System.currentTimeMillis());

        dubboBeansCache.forEach((key, serviceBean) -> collect(serviceBean));

        // 校验path是否有重复
        Set<OasServiceMeta> allOpenApi = DubboOasDataCache.getAllOpenApi();
        if (!ObjectUtils.isEmpty(allOpenApi)) {
            Set<String> pathSet = new HashSet<>();
            for (OasServiceMeta oasServiceMeta : allOpenApi) {
                Set<OasMethod> methods = oasServiceMeta.getMethods();
                if (ObjectUtils.isEmpty(methods)) {
                    continue;
                }
                for (OasMethod method : methods) {
                    String path = method.getPath();
                    String httpMethod = method.getHttpMethod();
                    if (!pathSet.add(path+httpMethod)) {
                        String msg = "path: " + path +"  is duplicate!!";
                        throw new Exception(msg);
                    }
                }
            }
        }
    }

    public void collectOpenApiData(ServiceBean bean) {
        Object serviceBeanRef = bean.getRef();
        collect(Objects.requireNonNull(dubboBeansCache.putIfAbsent(serviceBeanRef.getClass().getCanonicalName(), bean)));
    }

    @SneakyThrows
    private void collect(ServiceBean bean) {
        Object serviceBeanRef = bean.getRef();
        Class<?> apiModuleClass;
        if (AopUtils.isAopProxy(serviceBeanRef)) {
            apiModuleClass = AopUtils.getTargetClass(serviceBeanRef);
        } else {
            apiModuleClass = serviceBeanRef.getClass();
        }
        OasServiceMeta serviceMeta = oasGenerator.generateOasData(apiModuleClass);
        if (serviceMeta != null) {
            serviceMeta.setGroup(bean.getGroup());
            DubboOasDataCache.addOpenApi(apiModuleClass.getInterfaces()[0].getTypeName(), serviceMeta);
            log.info("{} OpenApi:{}", apiModuleClass.getTypeName(), Json.pretty(serviceMeta));
        }
    }

    public OasApplicationMeta getOasApplicationMeta() {
        return oasApplicationMeta;
    }

    @PreDestroy
    public void destroy() {
        dubboBeansCache.clear();
    }


}
