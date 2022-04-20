package com.uyibai.microservice.toolkit.oas.starter;

import com.uyibai.microservice.toolkit.oas.model.OasServiceMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * OpenApi Cache
 */
public class DubboOasDataCache {

    private static final Map<String, OasServiceMeta> OAS_CACHE_MAP = new HashMap<>();

    private static Set<OasServiceMeta> allOpenApi = null;

    public static void addOpenApi(String key, OasServiceMeta oasServiceMeta) {
        OAS_CACHE_MAP.put(key, oasServiceMeta);
    }

    public static OasServiceMeta getOpenApi(String key) {
        return OAS_CACHE_MAP.get(key);
    }

    public static Set<OasServiceMeta> getAllOpenApi() {
        if (allOpenApi == null) {
            allOpenApi = new HashSet<>(OAS_CACHE_MAP.size());
            OAS_CACHE_MAP.forEach((k, v) -> {
                allOpenApi.add(v);
            });
        }
        return allOpenApi;
    }

    public static Set<String> getAllKey(){
        return OAS_CACHE_MAP.keySet();
    }

}
