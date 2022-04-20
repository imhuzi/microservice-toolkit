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
import com.uyibai.microservice.toolkit.oas.model.OasServiceMeta;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

public class OasGenerator {

    private static List<OpenApiAnnotationParser> parserList = new ArrayList<>();

    static {
        ServiceLoader.load(OpenApiAnnotationParser.class).forEach(parserList::add);
        parserList.sort(Comparator.comparingInt(OpenApiAnnotationParser::getOrder));
    }

    public OpenAPI generate(Class<?> cls) {

        Optional<OpenApiAnnotationParser> parserOptional = parserList.stream().filter(parser -> parser.canProcess(cls))
                .findFirst();

        if (!parserOptional.isPresent()) {
            return null;
        }
        OasContext context = new OasContext(parserOptional.get());
        context.setCls(cls);
        parserOptional.get().parser(cls, context);
        return context.toOpenAPI();
    }

    public List<OpenAPI> generate(Set<Class> classes) {
        return classes.parallelStream().map(this::generate).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public OasServiceMeta generateOasData(Class<?> cls) {

        Optional<OpenApiAnnotationParser> parserOptional = parserList.stream().filter(parser -> parser.canProcess(cls))
                .findFirst();

        if (!parserOptional.isPresent()) {
            return null;
        }
        OasContext context = new OasContext(parserOptional.get());
        context.setCls(cls);
        parserOptional.get().parser(cls, context);
        OasServiceMeta oasServiceMeta = context.toOasData();
        oasServiceMeta.setSchemas(context.toOasSchemas());
        return oasServiceMeta;
    }
}
