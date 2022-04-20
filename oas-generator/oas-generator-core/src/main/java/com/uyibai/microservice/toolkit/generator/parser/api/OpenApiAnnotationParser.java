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

package com.uyibai.microservice.toolkit.generator.parser.api;


import com.uyibai.microservice.toolkit.generator.annotation.ClassAnnotationProcessor;
import com.uyibai.microservice.toolkit.generator.annotation.MethodAnnotationProcessor;
import com.uyibai.microservice.toolkit.generator.annotation.ParamAnnotationProcessor;
import com.uyibai.microservice.toolkit.generator.context.OasContext;
import com.uyibai.microservice.toolkit.generator.context.OperationContext;
import com.uyibai.microservice.toolkit.generator.context.ParameterContext;

import java.lang.annotation.Annotation;

public interface OpenApiAnnotationParser {

  /**
   *
   * @param cls
   * @param context
   */
  void parser(Class<?> cls, OasContext context);

  /**
   * Used for sorting
   * For the same parsing class, only the same programming model can be parsed at the same time.
   * @return
   */
  int getOrder();

  boolean canProcess(Class<?> cls);

  void postParseClassAnnotaion(OasContext context);

  void postParseMethodAnnotation(OperationContext context);

  void postParseParameterAnnotation(ParameterContext context);

  ClassAnnotationProcessor findClassAnnotationProcessor(Class<? extends Annotation> annotationType);

  MethodAnnotationProcessor findMethodAnnotationProcessor(Class<? extends Annotation> annotationType);

  ParamAnnotationProcessor findParameterAnnotationProcessor(Class<? extends Annotation> annotationType);
}
