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

package plus.scg.microservice.toolkit.generator;

import plus.scg.microservice.toolkit.generator.annotation.ModelInterceptor;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Type;

public class MultipartFileInterceptor implements ModelInterceptor {

  @Override
  public int order() {
    return 100;
  }

  @Override
  public Schema process(Type cls, Components components) {

    if (!MultipartFile.class.equals(cls)) {
      return null;
    }

    return new FileSchema();
  }
}
