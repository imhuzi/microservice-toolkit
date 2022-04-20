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

package com.uyibai.microservice.toolkit.generator.context;

import com.uyibai.microservice.toolkit.generator.parser.api.OpenApiAnnotationParser;
import com.uyibai.microservice.toolkit.oas.model.OasMethod;
import com.uyibai.microservice.toolkit.oas.model.OasSchema;
import com.uyibai.microservice.toolkit.oas.model.OasServiceMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.PathItem.HttpMethod;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OasContext implements IExtensionsContext {

    private final OpenAPI openAPI;

    private final OasServiceMeta oasServiceMeta;

    private String basePath;

    private Info info = null;

    private Class<?> cls;

    private final List<OperationContext> operationList = new ArrayList<>();

    private OpenApiAnnotationParser parser;

    private final List<ISchemaContext> schemaCtxList = new ArrayList<>();

    private String httpMethod;

    private String[] consumes;

    private String[] produces;

    private String[] headers;

    public OasContext(OpenApiAnnotationParser parser) {
        this(new OpenAPI(), parser);
    }

    public OasContext(OpenAPI openAPI, OpenApiAnnotationParser parser) {
        this.openAPI = openAPI;
        this.parser = parser;
        this.oasServiceMeta = new OasServiceMeta();
    }

    public OasServiceMeta toOasData() {
        ensurePaths();
        correctBasepath();
        oasServiceMeta.setUriPrefix(basePath);
        if (info == null || openAPI.getInfo() == null) {
            openAPI.info(new Info().title("Undifined Title").version("1.0.0"));
        }
        if (info != null) {
            openAPI.setInfo(info);
        }

        Info info = openAPI.getInfo();
        if (info != null && info.getExtensions()!=null) {
            oasServiceMeta.setVersion(info.getVersion());
            oasServiceMeta.setService(info.getExtensions().get("x-java-interface").toString());
            oasServiceMeta.setType(info.getExtensions().get("x-microservice-type").toString());
        }
        Set<OasMethod> methodSet = operationList.stream().filter(OperationContext::hasOperation).map(OperationContext::toOasMethod).collect(Collectors.toSet());
        oasServiceMeta.setMethods(methodSet);
        return oasServiceMeta;
    }

    public Map<String, OasSchema> toOasSchemas() {
        schemaCtxList.forEach(schemaCtx -> openAPI.schema(schemaCtx.getSchema().getName(), schemaCtx.getSchema()));

        Map<String, Schema> schemaMap =  openAPI.getComponents().getSchemas();
        Map<String, OasSchema> data = new HashMap<>();
        if(schemaMap != null){
            schemaMap.forEach((key, schema) -> {
                try {
                    data.put(key, Json.mapper().readValue(Json.mapper().writeValueAsString(schema), OasSchema.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
        return data;
    }

    public OpenAPI toOpenAPI() {
        ensurePaths();
        for (OperationContext operationCtx : operationList) {
            if (!operationCtx.hasOperation()) {
                continue;
            }

            if (openAPI.getPaths() == null) {
                openAPI.setPaths(new Paths());
            }

            PathItem pathItem = openAPI.getPaths().get(operationCtx.getPath());
            if (pathItem == null) {
                pathItem = new PathItem();
                openAPI.path(operationCtx.getPath(), pathItem);
            }
            if (StringUtils.isNotBlank(operationCtx.getSummary())) {
                pathItem.setSummary(operationCtx.getSummary());
            }
            if (StringUtils.isNotBlank(operationCtx.getDescription())) {
                pathItem.setDescription(operationCtx.getDescription());
            }
            if (operationCtx.getExtensions() != null) {
                pathItem.setExtensions(operationCtx.getExtensions());
            }
            pathItem.operation(HttpMethod.valueOf(operationCtx.getHttpMethod()), operationCtx.toOperation());
        }

        // return null if there is no restful resource
        if (openAPI.getPaths() == null || openAPI.getPaths().size() == 0) {
            return null;
        }
        if (info == null || openAPI.getInfo() == null) {
            openAPI.info(new Info().title("Undifined Title").version("1.0.0"));
        }
        if (info != null) {
            openAPI.setInfo(info);
        }
        correctBasepath();
        correctComponents();

        openAPI.servers(Collections.singletonList(new Server().url(basePath)));
        schemaCtxList.forEach(schemaCtx -> openAPI.schema(schemaCtx.getSchema().getName(), schemaCtx.getSchema()));
        return openAPI;
    }

    private void correctComponents() {
        Components nullComponents = new Components();
        if (nullComponents.equals(getComponents())) {
            openAPI.setComponents(null);
        }
    }

    private void correctBasepath() {
        if (StringUtils.isEmpty(basePath)) {
            basePath = "/";
        }

        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
    }

    public Components getComponents() {
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(new Components());
        }
        return openAPI.getComponents();
    }

    private void ensurePaths() {
        if (openAPI.getPaths() == null) {
            openAPI.setPaths(new Paths());
        }
    }

    @Override
    public OpenApiAnnotationParser getParser() {
        return parser;
    }

    public void setParser(OpenApiAnnotationParser parser) {
        this.parser = parser;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public OpenAPI getOpenAPI() {
        return openAPI;
    }

    public String getBasePath() {
        return basePath;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void addOperation(OperationContext operation) {
        operationList.add(operation);
    }

    @Override
    public void addExtension(String name, Object value) {
        openAPI.addExtension(name, value);
    }

    @Override
    public Map<String, Object> getExtensions() {
        return openAPI.getExtensions();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String[] getConsumers() {
        return consumes;
    }

    public void setConsumers(String[] consumes) {
        this.consumes = consumes;
    }

    public String[] getProduces() {
        return produces;
    }

    public void setProduces(String[] produces) {
        this.produces = produces;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }
}
