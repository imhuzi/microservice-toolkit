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

package plus.scg.microservice.toolkit.generator.context;

import plus.scg.microservice.toolkit.generator.parser.api.OpenApiAnnotationParser;
import plus.scg.microservice.toolkit.generator.util.ModelConverter;
import plus.scg.microservice.toolkit.generator.util.ParamUtils;
import plus.scg.microservice.toolkit.generator.util.RequestResponse;
import plus.scg.microservice.toolkit.oas.model.OasParameter;
import io.swagger.v3.core.util.ParameterProcessor;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParameterContext implements ISchemaContext, IExtensionsContext {

    private final static Schema nullSchema = new Schema();

    private OperationContext parentContext;

    private Parameter parameter;

    private Object defaultValue;

    private io.swagger.v3.oas.models.parameters.Parameter oasParameter = new io.swagger.v3.oas.models.parameters.Parameter();

    private String name = null;

    private InType in = null;

    private Integer index = 0;

    private String description = null;

    private Boolean required = null;

    private Boolean deprecated = null;

    private Boolean allowEmptyValue = null;

    private ParameterStyle style;

    private Boolean explode = null;

    private Boolean allowReserved = null;

    private Schema schema;

    private Object example = null;

    private Map<String, Example> examples = null;

    private Content content = null;

    private String ref = null;

    private RequestBody requestBody;

    private List<String> consumes;

    public ParameterContext(OperationContext parentContext, Parameter parameter) {
        this.parentContext = parentContext;
        this.parameter = parameter;
        parentContext.addParamCtx(this);
    }

    public io.swagger.v3.oas.models.parameters.Parameter toParameter() {

        if (parameter == null) {
            return null;
        }
        ensureName();
        if (schema == null || nullSchema.equals(schema)) {
            schema = ModelConverter.getSchema(parameter.getType(), getComponents(), RequestResponse.REQUEST);
            oasParameter.schema(schema);
        }

        if (in == null) {
            oasParameter.setIn(ParameterIn.QUERY.toString());
        } else {
            switch (in) {
                case PATH: {
                    oasParameter.setIn(ParameterIn.PATH.toString());
                    break;
                }
                case QUERY: {
                    oasParameter.setIn(ParameterIn.QUERY.toString());
                    break;
                }
                case COOKIE: {
                    oasParameter.setIn(ParameterIn.COOKIE.toString());
                    break;
                }
                case HEADER: {
                    oasParameter.setIn(ParameterIn.HEADER.toString());
                    break;
                }
                default:
                    oasParameter.setIn(ParameterIn.QUERY.toString());
            }
        }

        if (defaultValue != null) {
            required = false;
            oasParameter.getSchema().setDefault(defaultValue);
        }

        oasParameter.setRequired(required);
        oasParameter.addExtension("x-index", index);
        return oasParameter;
    }

    public OasParameter toOasParameter() {
        if (getExtensions() != null && getExtensions().containsKey("x-hidden")) {
            return null;
        }
        io.swagger.v3.oas.models.parameters.Parameter oldParam = toParameter();
        OasParameter param = new OasParameter();
        param.setName(oldParam.getName());
        param.setIn(oldParam.getIn());
        param.setRequired(oldParam.getRequired() == null ? true : oldParam.getRequired());
        Schema schema = oldParam.getSchema();
        if (schema == null) {
            schema = new ObjectSchema();
        }
        // 如果 是 query 并且接收参数类型是自定义的 进行解析
//        if (InType.QUERY.name().toLowerCase().equals(param.getIn())) {
        parserRealType(param);
//        }
        param.setFormat(schema.getFormat());
        param.setRef(schema.get$ref());
        param.setIndex(index);
        return param;
    }

    /**
     * 如果 是单个参数，并且 httpMethod 是 Post，In fix 为 BODY
     *
     * @return
     */
    public InType getFixIn() {
        return (parentContext.isOneParam() && (parentContext.getHttpMethod().equals("POST") || parentContext.getHttpMethod().equals("PUT")) && isComplexType()) ? InType.BODY : in;
    }

    private boolean isComplexType() {
        String typeName = parameter.getType().getTypeName();
        if (!typeName.startsWith("java")) {
            return true;

        } else if (typeName.endsWith("Map")) { // Map
            return true;
        } else if (parameter.getType().isArray() && !parameter.getType().getComponentType().getTypeName().startsWith("java")) { // 非基本类型的数组
            return true;
        } else if (typeName.endsWith("List")) {
            String type = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0].getTypeName();
            return type.endsWith("Map") || !type.startsWith("java");
        } else if (typeName.endsWith("Set")) {
            String type = ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0].getTypeName();
            return type.endsWith("Map") || !type.startsWith("java");
        }
        return false;
    }

    public void parserRealType(OasParameter param) {

        String typeName = parameter.getType().getTypeName();
        param.setType(typeName);
        Class cls;
        if (parameter.getType().isArray()) {
            cls = parameter.getType().getComponentType();
            param.setItemType(cls.getTypeName());
            param.setItemProperties(ModelConverter.getRequestBeanTypeMap(cls));
        } else if (typeName.endsWith("List")) {
            param.setItemType(((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0].getTypeName());
            try {
                cls = Class.forName(param.getItemType());
                param.setItemProperties(ModelConverter.getRequestBeanTypeMap(cls));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (typeName.endsWith("Set")) {
            param.setItemType(((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0].getTypeName());
            try {
                cls = Class.forName(param.getItemType());
                param.setItemProperties(ModelConverter.getRequestBeanTypeMap(cls));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (!typeName.startsWith("java")) {
            cls = parameter.getType();
            param.setItemType(cls.getTypeName());
            param.setItemProperties(ModelConverter.getRequestBeanTypeMap(cls));
        }
    }

    public boolean isRequestBody() {
        if (in != null && (in.equals(InType.BODY) || in.equals(InType.FORM))) {
            return true;
        }
        return false;
    }

    public void applyAnnotations(List<Annotation> annotations) {
        ParameterProcessor
                .applyAnnotations(oasParameter, getType(), annotations,
                        getComponents(),
                        null, null, null);
    }

    private void ensureName() {
        if (StringUtils.isEmpty(name)) {
            // try get real type
            name = ParamUtils.getParameterName(parentContext.getMethod(), parameter);
        }

        if (StringUtils.isEmpty(name)) {
            name = parameter.getName();
        }

        oasParameter.setName(name);
    }

    public OperationContext getOperationContext() {
        return parentContext;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Components getComponents() {
        return parentContext.getComponents();
    }

    public io.swagger.v3.oas.models.parameters.Parameter getOasParameter() {
        return oasParameter;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public boolean isRequired() {
        return required;
    }

    public Type getType() {

        if (getRealType() != null) {
            return getRealType();
        }

        if (StringUtils.isNotEmpty(oasParameter.getIn())) {
            return ReflectionUtils.typeFromString(oasParameter.getIn());
        }

        return null;
    }

    public void addConsume(String consume) {
        if (consumes == null) {
            consumes = new ArrayList<>();
        }
        consumes.add(consume);
    }

    public List<String> getConsumers() {
        return consumes;
    }

    public Type getRealType() {
        return parameter.getParameterizedType();
    }

    public void setRequestBody(RequestBody requestBody) {
        required = requestBody.getRequired();
        description = requestBody.getDescription();
        this.requestBody = requestBody;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    @Override
    public Schema getSchema() {

        Schema refSchema = oasParameter.getSchema();
        if (refSchema == null || nullSchema.equals(refSchema)) {
            refSchema = ModelConverter.getSchema(parameter.getType(), getComponents(), RequestResponse.REQUEST);
            oasParameter.schema(refSchema);
        }

        return refSchema;
    }

    @Override
    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public void addExtension(String name, Object value) {
        oasParameter.addExtension(name, value);
    }

    @Override
    public Map<String, Object> getExtensions() {
        return oasParameter.getExtensions();
    }

    @Override
    public OpenApiAnnotationParser getParser() {
        return parentContext.getParser();
    }

    public String getName() {
        ensureName();
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InType getIn() {
        return in;
    }

    public void setIn(InType in) {
        this.in = in;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public Boolean getAllowEmptyValue() {
        return allowEmptyValue;
    }

    public void setAllowEmptyValue(Boolean allowEmptyValue) {
        this.allowEmptyValue = allowEmptyValue;
    }

    public ParameterStyle getStyle() {
        return style;
    }

    public void setStyle(ParameterStyle style) {
        this.style = style;
    }

    public Boolean getExplode() {
        return explode;
    }

    public void setExplode(Boolean explode) {
        this.explode = explode;
    }

    public Boolean getAllowReserved() {
        return allowReserved;
    }

    public void setAllowReserved(Boolean allowReserved) {
        this.allowReserved = allowReserved;
    }

    public Object getExample() {
        return example;
    }

    public void setExample(Object example) {
        this.example = example;
    }

    public Map<String, Example> getExamples() {
        return examples;
    }

    public void setExamples(Map<String, Example> examples) {
        this.examples = examples;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public enum InType {
        QUERY,
        PATH,
        HEADER,
        COOKIE,
        FORM,
        BODY
    }
}
