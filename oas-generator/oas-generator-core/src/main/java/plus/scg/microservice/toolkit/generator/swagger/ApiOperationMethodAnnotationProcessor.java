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

package plus.scg.microservice.toolkit.generator.swagger;


import plus.scg.microservice.toolkit.generator.annotation.MethodAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.context.OperationContext;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class ApiOperationMethodAnnotationProcessor implements MethodAnnotationProcessor<ApiOperation, OperationContext> {

  @Override
  public void process(ApiOperation annotation, OperationContext context) {
//    if (StringUtils.isNotBlank(annotation.nickname())) {
//      context.setOperationId(annotation.nickname());
//    }
    if (StringUtils.isNotBlank(annotation.httpMethod())) {
      context.setHttpMethod(annotation.httpMethod());
    }
    context.setSummary(annotation.value());
    context.setDescription(annotation.notes());

    Map<String, Object> extensionsFromAnnotation = Swagger2AnnotationUtils
        .getExtensionsFromAnnotation(annotation.extensions());
    Optional.ofNullable(extensionsFromAnnotation)
        .ifPresent(extensions -> extensions.forEach(context::addExtension));

    if (StringUtils.isNotBlank(annotation.consumes())) {
      context.setConsumers(annotation.consumes().split(","));
    }
    if (StringUtils.isNotBlank(annotation.produces())) {
      context.setProduces(annotation.produces().split(","));
    }
    // 是否隐藏
    if (annotation.hidden()){
      context.addExtension("x-hidden", annotation.hidden());
    }

    Arrays.stream(annotation.tags()).forEach(context::addTag);
  }
}
