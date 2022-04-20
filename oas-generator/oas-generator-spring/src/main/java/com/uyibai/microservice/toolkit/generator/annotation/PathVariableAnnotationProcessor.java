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

package com.uyibai.microservice.toolkit.generator.annotation;

import com.uyibai.microservice.toolkit.generator.context.ParameterContext;
import com.uyibai.microservice.toolkit.generator.util.ParamUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

public class PathVariableAnnotationProcessor implements ParamAnnotationProcessor<PathVariable, ParameterContext> {
  @Override
  public void process(PathVariable pathVariable, ParameterContext parameterContext) {
    String paramName = pathVariable.value();
    if (StringUtils.isEmpty(paramName)) {
      paramName = pathVariable.name();
    }

    if (StringUtils.isEmpty(paramName)) {
      paramName = ParamUtils.getParameterName(parameterContext.getOperationContext().getMethod(), parameterContext.getParameter());
    }

    parameterContext.setIn(ParameterContext.InType.PATH);
    parameterContext.setName(paramName);
    parameterContext.setRequired(pathVariable.required());
  }
}
