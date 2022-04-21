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
import org.springframework.web.bind.annotation.*;

public class SpringmvcAnnotationParser extends AbstractAnnotationParser {

  @Override
  public int getOrder() {
    return 200;
  }

  @Override
  public void parser(Class<?> cls, OasContext context) {
    super.parser(cls, context);
  }

  @Override
  public boolean canProcess(Class<?> cls) {
    if (cls.getAnnotation(RestController.class) != null) {
      return true;
    }
    return false;
  }

  @Override
  public void initClassAnnotationProcessor() {
    super.initClassAnnotationProcessor();
    classAnnotationMap.put(RequestMapping.class, new RequestMappingClassAnnotationProcessor());
  }

  @Override
  public void initMethodAnnotationProcessor() {
    super.initMethodAnnotationProcessor();
    methodAnnotationMap.put(RequestMapping.class, new RequestMappingMethodAnnotationProcessor());
    methodAnnotationMap.put(GetMapping.class, new GetMappingMethodAnnotationProcessor());
    methodAnnotationMap.put(PutMapping.class, new PutMappingMethodAnnotationProcessor());
    methodAnnotationMap.put(PostMapping.class, new PostMappingMethodAnnotationProcessor());
    methodAnnotationMap.put(DeleteMapping.class, new DeleteMappingMethodAnnotationProcessor());
  }

  @Override
  public void initParameterAnnotationProcessor() {
    super.initParameterAnnotationProcessor();
    parameterAnnotationMap.put(PathVariable.class, new PathVariableAnnotationProcessor());
    parameterAnnotationMap.put(RequestBody.class, new RequestBodyAnnotationProcessor());
    parameterAnnotationMap.put(RequestPart.class, new RequestPartAnnotationProcessor());
    parameterAnnotationMap.put(RequestParam.class, new RequestParamAnnotationProcessor());
    parameterAnnotationMap.put(RequestHeader.class, new RequestHeaderAnnotationProcessor());
  }
}

