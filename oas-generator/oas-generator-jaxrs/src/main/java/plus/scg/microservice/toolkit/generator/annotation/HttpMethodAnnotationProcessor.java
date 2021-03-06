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

import javax.ws.rs.HttpMethod;
import java.lang.annotation.Annotation;

public class HttpMethodAnnotationProcessor implements MethodAnnotationProcessor<Annotation, OperationContext> {
  @Override
  public void process(Annotation annotation, OperationContext operationContext) {
    HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
    operationContext.setHttpMethod(httpMethod.value());
  }
}
