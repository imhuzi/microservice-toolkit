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

package plus.scg.microservice.toolkit.generator.annotation;

import plus.scg.microservice.toolkit.generator.context.OperationContext;
import plus.scg.microservice.toolkit.generator.util.SwaggerAnnotationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class OperationMethodAnnotationProcessor implements MethodAnnotationProcessor<Operation, OperationContext> {

  @Override
  public void process(Operation annotation, OperationContext context) {

    context.setOperationId(annotation.operationId());
    context.setDeprecated(annotation.deprecated());
    context.setDescription(annotation.description());

    Map<String, Object> extensionsFromAnnotation = SwaggerAnnotationUtils
        .getExtensionsFromAnnotation(annotation.extensions());
    Optional.ofNullable(extensionsFromAnnotation)
        .ifPresent(extensions -> extensions.forEach(context::addExtension));

    ApiResponse[] responses = annotation.responses();
    MethodAnnotationProcessor apiResponseAnnotationProcessor = context.getParser()
        .findMethodAnnotationProcessor(ApiResponse.class);

    for (ApiResponse response : responses) {
      Optional.ofNullable(apiResponseAnnotationProcessor)
          .ifPresent(processor -> processor.process(response, context));
    }

    // 是否隐藏
    if (annotation.hidden()){
      context.addExtension("x-hidden", annotation.hidden());
    }

    if (StringUtils.isNotBlank(annotation.method())) {
      context.setHttpMethod(annotation.method());
    }
    context.setSummary(annotation.summary());
    context.setDescription(annotation.description());
    Arrays.stream(annotation.tags()).forEach(context::addTag);
  }
}
