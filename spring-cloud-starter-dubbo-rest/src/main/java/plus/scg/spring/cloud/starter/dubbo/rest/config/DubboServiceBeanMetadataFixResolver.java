package plus.scg.spring.cloud.starter.dubbo.rest.config;

import com.alibaba.cloud.dubbo.autoconfigure.DubboMetadataAutoConfiguration;
import com.alibaba.cloud.dubbo.metadata.RestMethodMetadata;
import com.alibaba.cloud.dubbo.metadata.ServiceRestMetadata;
import com.alibaba.cloud.dubbo.metadata.resolver.DubboServiceBeanMetadataResolver;
import feign.Contract;
import feign.Feign;
import feign.MethodMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(DubboMetadataAutoConfiguration.class)
public class DubboServiceBeanMetadataFixResolver extends DubboServiceBeanMetadataResolver {
    public DubboServiceBeanMetadataFixResolver(ObjectProvider<Contract> contractObjectProvider) {
        super(contractObjectProvider);
    }

    @Override
    public Set<ServiceRestMetadata> resolveServiceRestMetadata(ServiceBean serviceBean) {

        Object bean = serviceBean.getRef();

        Class<?> beanType = bean.getClass();
        // 如果是代理类,class取原类
        if (AopUtils.isAopProxy(bean)) {
            beanType = AopUtils.getTargetClass(bean);
        }

        Set<ServiceRestMetadata> serviceRestMetadata = new LinkedHashSet<>();

        Set<RestMethodMetadata> methodRestMetadata = resolveMethodRestMetadata(beanType);

        List<URL> urls = serviceBean.getExportedUrls();

        urls.stream().map(URL::toString).forEach(url -> {
            ServiceRestMetadata metadata = new ServiceRestMetadata();
            metadata.setUrl(url);
            metadata.setMeta(methodRestMetadata);
            serviceRestMetadata.add(metadata);
        });

        return serviceRestMetadata;
    }

    protected RestMethodMetadata resolveMethodRestMetadata(MethodMetadata methodMetadata,
                                                           Class<?> targetType, List<Method> feignContractMethods) {
        String configKey = methodMetadata.configKey();
        // 如果类上面RequestMapping注解有值,将path拼在方法的path前面
        RequestMapping annotation = targetType.getAnnotation(RequestMapping.class);
        if (annotation != null) {
            String[] values = annotation.value();
            if (values.length > 0) {
                String path = values[0];
                if (StringUtils.isNotBlank(path)) {
                    // 不是以/开头的话,拼上/
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    // 如果以/结尾,去掉
                    if (path.endsWith("/")) {
                        path = path.substring(0,path.length()-1);
                    }
                    String url = methodMetadata.template().url();
                    url = path + url;
                    methodMetadata.template().uri(url);
                }
            }
        }
        Method feignContractMethod = getMatchedFeignContractMethod(targetType,
                feignContractMethods, configKey);
        RestMethodMetadata metadata = new RestMethodMetadata(methodMetadata);
        metadata.setMethod(
                new com.alibaba.cloud.dubbo.metadata.MethodMetadata(feignContractMethod));
        return metadata;
    }

    private Method getMatchedFeignContractMethod(Class<?> targetType,
                                                 List<Method> methods, String expectedConfigKey) {
        Method matchedMethod = null;
        for (Method method : methods) {
            String configKey = Feign.configKey(targetType, method);
            if (expectedConfigKey.equals(configKey)) {
                matchedMethod = method;
                break;
            }
        }
        return matchedMethod;
    }

}
