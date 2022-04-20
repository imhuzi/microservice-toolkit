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

package com.uyibai.microservice.toolkit.generator.parser;

import com.uyibai.microservice.toolkit.generator.annotation.DubboClassAnnotationProcessor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class DubboSpringMvcParser extends SpringmvcAnnotationParser {

    @Override
    public boolean canProcess(Class<?> cls) {
        if (cls.getAnnotation(DubboService.class) != null
                && (cls.getAnnotation(RequestMapping.class) != null || cls.getAnnotation(RestController.class) != null)) {
            return true;
        }

        return false;
    }

    @Override
    public void initClassAnnotationProcessor() {
        classAnnotationMap.put(DubboService.class, new DubboClassAnnotationProcessor());
        super.initClassAnnotationProcessor();
    }

}
