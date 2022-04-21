/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package plus.scg.microservice.toolkit.generator.parser;

import plus.scg.microservice.toolkit.generator.annotation.*;
import plus.scg.microservice.toolkit.generator.context.OasContext;
import plus.scg.microservice.toolkit.generator.context.OperationContext;
import plus.scg.microservice.toolkit.generator.context.ParameterContext;
import plus.scg.microservice.toolkit.generator.parser.api.OpenApiAnnotationParser;
import plus.scg.microservice.toolkit.generator.swagger.ApiClassAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.swagger.ApiOperationMethodAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.swagger.ApiParamAnnotationProcessor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.PathItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public abstract class AbstractAnnotationParser implements OpenApiAnnotationParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String[] GET_STARTS = "get,find,query,select,list".split(",", -1);
    private final String[] POST_STARTS = "save,add,update,create".split(",", -1);
    private final String[] PUT_STARTS = "update,put".split(",", -1);
    private final String[] DELETE_STARTS = "delete,remove,del".split(",", -1);

    private Class<?> cls;

    private OasContext context;

    protected Map<Class<? extends Annotation>, ClassAnnotationProcessor> classAnnotationMap = new HashMap<>();

    protected Map<Class<? extends Annotation>, MethodAnnotationProcessor> methodAnnotationMap = new HashMap<>();

    protected Map<Class<? extends Annotation>, ParamAnnotationProcessor> parameterAnnotationMap = new HashMap<>();

    public AbstractAnnotationParser() {
        initMethodAnnotationProcessor();
        initClassAnnotationProcessor();
        initParameterAnnotationProcessor();
    }

    @Override
    public void parser(Class<?> cls, OasContext context) {

        this.cls = cls;
        this.context = context;

        if (!canProcess(cls)) {
            return;
        }

        for (Annotation clsAnnotation : cls.getAnnotations()) {
            AnnotationProcessor annotationProcessor = classAnnotationMap.get(clsAnnotation.annotationType());
            if (annotationProcessor == null) {
                continue;
            }
            annotationProcessor.process(clsAnnotation, context);
        }
        postParseClassAnnotaion(context);

        List<Method> methods = Arrays.asList(cls.getDeclaredMethods());

        if (isDubbo(context)) {
            // 如果是 dubbo 服务 就 排除掉 接口中不存在的方法
            List<String> interfaceMethods = Arrays.stream(cls.getInterfaces()[0].getDeclaredMethods()).map(Method::getName).collect(Collectors.toList());
            methods = methods.stream().filter(item -> interfaceMethods.contains(item.getName())).collect(Collectors.toList());
        }

        methods.sort(Comparator.comparing(Method::getName));
        for (Method m : methods) {
            OperationContext operationContext = new OperationContext(m, context);
            for (Annotation methodAnnotation : m.getAnnotations()) {
                MethodAnnotationProcessor annotationProcessor = methodAnnotationMap.get(methodAnnotation.annotationType());
                if (annotationProcessor != null) {
                    annotationProcessor.process(methodAnnotation, operationContext);
                }
            }

            postParseMethodAnnotation(operationContext);

            java.lang.reflect.Parameter[] parameters = m.getParameters();

            for (java.lang.reflect.Parameter parameter : parameters) {
                ParameterContext parameterContext = new ParameterContext(operationContext, parameter);
                for (Annotation paramAnnotation : parameter.getAnnotations()) {
                    ParamAnnotationProcessor paramAnnotationProcessor = parameterAnnotationMap.get(paramAnnotation.annotationType());
                    if (paramAnnotationProcessor != null) {
                        paramAnnotationProcessor.process(paramAnnotation, parameterContext);
                    }
                }
                postParseParameterAnnotation(parameterContext);
            }
        }
    }

    @Override
    public void postParseClassAnnotaion(OasContext context) {
        String basePath = context.getBasePath();
        basePath = getStandardPath(basePath);
        context.setBasePath(basePath);
    }

    private String getStandardPath(String basePath) {
        if (StringUtils.isNotBlank(basePath)) {
            if (!basePath.startsWith("/")) {
                basePath = "/" + basePath;
            }
            if (basePath.endsWith("/")) {
                basePath = basePath.substring(0,basePath.length()-1);
            }
        }
        return basePath;
    }

    @Override
    public void postParseMethodAnnotation(OperationContext context) {
        // 如果 是 dubbo 补全方法信息
        if (isDubbo(this.context)) {
            Method currentMethod = context.getMethod();
            if (StringUtils.isEmpty(context.getOperationId())) {
                context.setOperationId(currentMethod.getName());
            }
            if (StringUtils.isEmpty(context.getHttpMethod())) {
                context.setHttpMethod(correctHttpMethod(context.getOperationId()));
            }

            if (StringUtils.isEmpty(context.getPath())) {
                context.setPath(correctPath(currentMethod.getName()));
            }

            if (context.getApiResponses() == null || context.getApiResponses().size() == 0) {
                context.correctResponse(context.getApiResponses());
            }
            context.setPath(getStandardPath(context.getPath()));

        }
    }

    @Override
    public void postParseParameterAnnotation(ParameterContext context) {
        context.setIn(context.getFixIn());
    }

    public void initMethodAnnotationProcessor() {
        methodAnnotationMap.put(Operation.class, new OperationMethodAnnotationProcessor());
        methodAnnotationMap.put(ApiResponse.class, new ApiResponseMethodAnnotationProcessor());
        methodAnnotationMap.put(ApiResponses.class, new ApiResponsesMethodAnnotationProcessor());
        methodAnnotationMap.put(ApiOperation.class, new ApiOperationMethodAnnotationProcessor());
    }

    public void initClassAnnotationProcessor() {
        classAnnotationMap.put(OpenAPIDefinition.class, new OpenApiDefinitionClassAnnotationProcessor());
        classAnnotationMap.put(Tag.class, new OpenApiTagClassAnnotationProcessor());
        classAnnotationMap.put(Tags.class, new OpenApiTagsClassAnnotationProcessor());
        classAnnotationMap.put(Api.class, new ApiClassAnnotationProcessor());
    }

    public void initParameterAnnotationProcessor() {
        parameterAnnotationMap.put(Parameter.class, new ParameterAnnotationProcessor());
        parameterAnnotationMap.put(RequestBody.class, new RequestBodyParamAnnotationProcessor());
        parameterAnnotationMap.put(ApiParam.class, new ApiParamAnnotationProcessor());
    }

    @Override
    public ClassAnnotationProcessor findClassAnnotationProcessor(Class<? extends Annotation> annotationType) {
        return classAnnotationMap.get(annotationType);
    }

    @Override
    public MethodAnnotationProcessor findMethodAnnotationProcessor(Class<? extends Annotation> annotationType) {
        return methodAnnotationMap.get(annotationType);
    }

    @Override
    public ParamAnnotationProcessor findParameterAnnotationProcessor(Class<? extends Annotation> annotationType) {
        return parameterAnnotationMap.get(annotationType);
    }

    private boolean isDubbo(OasContext context) {
        Map<String, Object> extensions = context.getInfo().getExtensions();
        Object type = extensions.get("x-microservice-type");
        return type != null && ("DubboRest".equals(type.toString()) || ("Dubbo").equals(type.toString()));
    }


    private String correctPath(String path) {
        if (path == null || path.startsWith("/")) {
            return path;
        }
        return "/" + path;
    }

    /**
     * 根据 operationId covert  http method
     *
     * @param operationId
     * @return
     */
    private String correctHttpMethod(String operationId) {
        // 默认为 get
        AtomicReference<PathItem.HttpMethod> method = new AtomicReference<>();
        Arrays.stream(GET_STARTS).filter(operationId::startsWith).findFirst().ifPresent(item -> {
            method.set(PathItem.HttpMethod.GET);
        });
        if (method.get() == null) {
            Arrays.stream(POST_STARTS).filter(operationId::startsWith).findFirst().ifPresent(item -> {
                method.set(PathItem.HttpMethod.POST);
            });
        }
        if (method.get() == null) {
            Arrays.stream(PUT_STARTS).filter(operationId::startsWith).findFirst().ifPresent(item -> {
                method.set(PathItem.HttpMethod.PUT);
            });
        }
        if (method.get() == null) {
            Arrays.stream(DELETE_STARTS).filter(operationId::startsWith).findFirst().ifPresent(item -> {
                method.set(PathItem.HttpMethod.DELETE);
            });
        }
        if (method.get() == null) {
            method.set(PathItem.HttpMethod.GET);
        }
        return method.get().name();
    }
}
