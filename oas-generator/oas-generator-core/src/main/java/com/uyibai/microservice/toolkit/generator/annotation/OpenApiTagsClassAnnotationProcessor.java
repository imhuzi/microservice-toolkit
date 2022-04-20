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

import com.uyibai.microservice.toolkit.generator.context.OasContext;

import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.info.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OpenApiTagsClassAnnotationProcessor implements
        ClassAnnotationProcessor<Tags, OasContext> {

    @Override
    public void process(Tags tagAnnotations, OasContext context) {
        List<io.swagger.v3.oas.models.tags.Tag> tagList = new ArrayList<>();
        for (io.swagger.v3.oas.annotations.tags.Tag tagAnnotation : tagAnnotations.value()) {
            io.swagger.v3.oas.models.tags.Tag tag = new io.swagger.v3.oas.models.tags.Tag();
            tag
                    .name(tagAnnotation.name())
                    .description(tagAnnotation.description());
            tagList.add(tag);
        }
        context.getOpenAPI().tags(tagList);
        // 如果 类上加了 tag 标签 解析第一个 放到 title中
        if (tagList.size()>0) {
            Info info =  context.getInfo();
            if (info == null) {
                info = new Info();
            }
            io.swagger.v3.oas.models.tags.Tag tag = tagList.get(0);
            info.title(tag.getName()).description(tag.getDescription());
            info.addExtension("x-tags", tagList.stream().map(io.swagger.v3.oas.models.tags.Tag::getName).collect(Collectors.joining(",")));
            context.setInfo(info);
        }
    }
}
