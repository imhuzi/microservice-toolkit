package com.uyibai.microservice.toolkit.generator.annotation;

import com.uyibai.microservice.toolkit.generator.context.OasContext;
import com.google.common.base.CaseFormat;
import io.swagger.v3.oas.models.info.Info;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Path;

public class DubboClassAnnotationProcessor implements
        ClassAnnotationProcessor<DubboService, OasContext> {

    @Override
    public void process(DubboService dubboService, OasContext oasContext) {
        // context 中的base path 没有被设置的时候才会 兼容处理
        if (StringUtils.isBlank(oasContext.getBasePath())) {
            String path = dubboService.path();
            if (StringUtils.isBlank(path)) {
                path = oasContext.getCls().getSimpleName()
                        .replace("RpcServiceImpl", "")
                        .replace("RestService", "")
                        .replace("RestServiceImpl", "")
                        .replaceAll("ServiceImpl", "");
                String pathModel = System.getProperty("Path.Model");
                // 如果 path 模式 是斜杠分隔
                if (pathModel != null && pathModel.equals("sprit")) {
                    path = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, path).replaceAll("_", "/");
                } else {
                    // 否则，就是 去掉 RpcServiceImpl 首字母转小写
                    path = StringUtils.uncapitalize(path);
                }
            }
            oasContext.setBasePath(path);
        }
        Class<?>[] classes = oasContext.getCls().getInterfaces();
        Info info =  oasContext.getInfo();
        if (info == null) {
            info =  new Info().title(classes[0].getSimpleName() + "Api");
        }
        info.version(dubboService.version());
//        info.addExtension("x-java-interface", classes[0].getName());
        boolean isDubboRest = oasContext.getCls().getAnnotation(RestController.class) != null
                || oasContext.getCls().getAnnotation(RequestMapping.class) != null
                || oasContext.getCls().getAnnotation(Path.class) != null;
        info.addExtension("x-java-interface", classes[0].getTypeName());
        // 微服务类型
        info.addExtension("x-microservice-type", isDubboRest ? "DubboRest" : "Dubbo");
        oasContext.setInfo(info);
    }
}
