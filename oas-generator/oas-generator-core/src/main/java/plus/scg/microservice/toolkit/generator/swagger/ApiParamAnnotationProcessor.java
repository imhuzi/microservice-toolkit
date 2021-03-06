package plus.scg.microservice.toolkit.generator.swagger;

import plus.scg.microservice.toolkit.generator.annotation.ParamAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.context.ParameterContext;
import io.swagger.annotations.ApiParam;

public class ApiParamAnnotationProcessor implements ParamAnnotationProcessor<ApiParam, ParameterContext> {

    @Override
    public void process(ApiParam apiParam, ParameterContext paramCtx) {
        paramCtx.setRequired(apiParam.required());
        paramCtx.setAllowEmptyValue(apiParam.allowEmptyValue());
        paramCtx.setDefaultValue(apiParam.defaultValue());
        paramCtx.setDescription(apiParam.value());
        paramCtx.setName(apiParam.name());
        paramCtx.setExample(apiParam.example());
    }
}
