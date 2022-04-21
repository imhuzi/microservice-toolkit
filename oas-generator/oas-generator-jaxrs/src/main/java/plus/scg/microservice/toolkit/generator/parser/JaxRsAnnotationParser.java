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

import plus.scg.microservice.toolkit.generator.annotation.ConsumesAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.CookieParamAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.FormParamAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.HeaderParamAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.HttpMethodAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.PathClassAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.PathMethodAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.PathParamAnnotationProcessor;
import plus.scg.microservice.toolkit.generator.annotation.QueryParamAnnotationProcessor;
import io.swagger.v3.oas.annotations.headers.Header;

import javax.ws.rs.*;

public class JaxRsAnnotationParser extends AbstractAnnotationParser {

  @Override
  public void initClassAnnotationProcessor() {
    super.initClassAnnotationProcessor();
    classAnnotationMap.put(Path.class, new PathClassAnnotationProcessor());
  }

  @Override
  public void initMethodAnnotationProcessor() {
    super.initMethodAnnotationProcessor();
    methodAnnotationMap.put(Path.class, new PathMethodAnnotationProcessor());

    HttpMethodAnnotationProcessor httpMethodAnnotationProcessor = new HttpMethodAnnotationProcessor();
    methodAnnotationMap.put(GET.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(POST.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(DELETE.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(PATCH.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(PUT.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(OPTIONS.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(HEAD.class, httpMethodAnnotationProcessor);
    methodAnnotationMap.put(Consumes.class, new ConsumesAnnotationProcessor());
  }

  @Override
  public void initParameterAnnotationProcessor() {
    super.initParameterAnnotationProcessor();

    parameterAnnotationMap.put(QueryParam.class, new QueryParamAnnotationProcessor());
    parameterAnnotationMap.put(CookieParam.class, new CookieParamAnnotationProcessor());
    parameterAnnotationMap.put(FormParam.class, new FormParamAnnotationProcessor());
    parameterAnnotationMap.put(PathParam.class, new PathParamAnnotationProcessor());
    parameterAnnotationMap.put(Header.class, new HeaderParamAnnotationProcessor());
  }

  @Override
  public int getOrder() {
    return 100;
  }

  @Override
  public boolean canProcess(Class<?> cls) {

    if (cls.getAnnotation(Path.class) != null) {
      return true;
    }
    return false;
  }
}
