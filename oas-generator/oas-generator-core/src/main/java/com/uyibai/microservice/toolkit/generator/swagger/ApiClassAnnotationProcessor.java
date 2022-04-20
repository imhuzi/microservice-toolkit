package com.uyibai.microservice.toolkit.generator.swagger;

import com.uyibai.microservice.toolkit.generator.annotation.ClassAnnotationProcessor;
import com.uyibai.microservice.toolkit.generator.context.OasContext;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.models.info.Info;
import org.apache.commons.lang3.StringUtils;

public class ApiClassAnnotationProcessor implements ClassAnnotationProcessor<Api, OasContext> {
    @Override
    public void process(Api api, OasContext context) {
        Info info = context.getInfo();
        if (info == null) {
            info = new Info();
        }
        info.title(api.value());
        info.addExtension("x-tags", api.tags());
        context.setInfo(info);
        if (StringUtils.isNotBlank(api.basePath())) {
            context.setBasePath(api.basePath());
        }
        if (StringUtils.isNotBlank(api.consumes())) {
            context.setConsumers(api.consumes().split(","));
        }
        if (StringUtils.isNotBlank(api.produces())) {
            context.setProduces(api.produces().split(","));
        }
    }
}
