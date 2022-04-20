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

package com.uyibai.microservice.toolkit.generator;

import com.uyibai.microservice.common.api.ResData;
import com.uyibai.microservice.toolkit.generator.context.OasGenerator;
import com.uyibai.microservice.toolkit.generator.parser.DubboJaxrsParser;
import com.uyibai.microservice.toolkit.generator.parser.DubboPojoParser;
import com.uyibai.microservice.toolkit.generator.parser.DubboSpringMvcParser;
import com.uyibai.microservice.toolkit.oas.model.OasServiceMeta;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;

import org.apache.dubbo.config.annotation.DubboService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import java.util.List;
import java.util.Map;

public class ServiceCombParserTest {

    @Test
    public void parse() {

        DubboJaxrsParser dubboJaxrsParser = new DubboJaxrsParser();
        DubboPojoParser dubboPojoParser = new DubboPojoParser();
        DubboSpringMvcParser dubboSpringmvcParser = new DubboSpringMvcParser();

        boolean canProcess = dubboJaxrsParser.canProcess(DubboJaxrsServiceImpl.class);
        Assert.assertTrue(canProcess);

        canProcess = dubboJaxrsParser.canProcess(DubboPojoServiceImpl.class);
        Assert.assertFalse(canProcess);

        canProcess = dubboSpringmvcParser.canProcess(DubboSpringmvcDemoServiceImpl.class);
        Assert.assertTrue(canProcess);
        canProcess = dubboSpringmvcParser.canProcess(DubboPojoServiceImpl.class);
        Assert.assertFalse(canProcess);

        canProcess = dubboPojoParser.canProcess(DubboPojoServiceImpl.class);
        Assert.assertTrue(canProcess);
        canProcess = dubboPojoParser.canProcess(DubboSpringmvcDemoServiceImpl.class);
        Assert.assertTrue(canProcess);
//        Assert.assertEquals(100, dubboPojoParser.getOrder());
//
//        OasContext pojoOasContext = new OasContext(dubboSpringmvcParser);
//        dubboPojoParser.parser(DubboSpringmvcDemoServiceImpl.class, pojoOasContext);
//
//        try {
//            String swaggerJson = Json.mapper().writeValueAsString(pojoOasContext.toOpenAPI());
//            System.out.println("json:" + swaggerJson);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }

//        Assert.assertNull(pojoOasContext.getBasePath());
    }

    @Test
    public void generate() {
        OasGenerator oasGenerator = new OasGenerator();
//        OpenAPI dubboSpringMvcOas = oasGenerator.generate(DubboJaxrsServiceImpl.class);
//        OpenAPI dubboSpringMvcOpenApi = oasGenerator.generate(DubboJaxrsServiceImpl.class);
        OasServiceMeta dubboJaxrsOas = oasGenerator.generateOasData(DubboPojoServiceImpl.class);
//        OpenAPI dubboPojoOas = oasGenerator.generate(DubboPojoServiceImpl.class);
        try {
//            String swaggerJson = Json.pretty().writeValueAsString(dubboSpringMvcOas);
//            System.out.println("json:" + swaggerJson);

            String json = Json.pretty().writeValueAsString(dubboJaxrsOas);
            System.out.println("json:" + json);
//
//            String ol = Json.pretty().writeValueAsString(dubboSpringMvcOpenApi);
//            System.out.println("json:" + ol);
//
//            String swaggerJson2 = Json.pretty().writeValueAsString(dubboPojoOas);
//            System.out.println("json:" + swaggerJson2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    @DubboService
    @Path("/pdemo")
    class DubboJaxrsServiceImpl implements DemoService {

        @Path("/path")
        @GET
        public String path(Integer age) {
            return null;
        }

        @Override
        public String getName(String userId) {
            return null;
        }

        @Override
        @GET
        public UserInfo create(UserInfo userInfo) {
            return null;
        }

        @Override
        @POST
        public UserInfo batchCreate(List<UserInfo> users) {
            return null;
        }

        @Override
        public ResData<UserInfo> batchArray(UserInfo[] users) {
            return null;
        }

        @Override
        public void batchMap(Map<String, UserInfo> users) {
        }

        public UserInfo batch(List<UserInfo> users) {
            return null;
        }

    }

    @DubboService
    @RequestMapping(path = "/demo")
    @OpenAPIDefinition(info = @Info(title="Demo "))
    class DubboSpringmvcDemoServiceImpl implements DemoService {

        @Override
        @GetMapping("/path/v2")
        public String path(Integer age) {
            return null;
        }

        @Override
        @PostMapping("/path/v2")
        @Operation(summary = "可看到卡士大夫")
        public String getName(String userId) {
            return "kkkk";
        }

        @Override
        @PostMapping("/user/create")
        public UserInfo create(@RequestParam UserInfo userInfo) {
            return null;
        }

        @Override
        public UserInfo batchCreate(@RequestBody  List<UserInfo> users) {
            return null;
        }

        @Override
        public ResData<UserInfo> batchArray(@RequestBody UserInfo[] users) {
            return null;
        }

        @Override
        public void batchMap(@RequestBody  Map<String, UserInfo> users) {
        }

        @PostMapping("/batch")
        public UserInfo batch(List<UserInfo> users) {
            return null;
        }

    }

    @DubboService
    @Api("Dubbo ServiceApi")
    @RequestMapping(path = "/demo")
    class DubboPojoServiceImpl implements DemoService {

        @ApiOperation(value = "path接口")
        public String path(Integer age) {
            return null;
        }

        @Override
        @DeleteMapping(path = "/{userId}")
        public String getName(@PathVariable String userId) {
            return null;
        }

        @Override
        public UserInfo create(UserInfo userInfo) {
            return null;
        }

        @Override
        public UserInfo batchCreate(List<UserInfo> users) {
            return null;
        }

        @Override
        public ResData<UserInfo> batchArray(UserInfo[] users) {
            return null;
        }

        @Override
        public void batchMap(Map<String, UserInfo> users) {
        }

        public UserInfo batch(List<UserInfo> users) {
            return null;
        }

    }
}
