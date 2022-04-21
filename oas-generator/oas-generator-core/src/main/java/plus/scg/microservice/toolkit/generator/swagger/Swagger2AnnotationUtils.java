package plus.scg.microservice.toolkit.generator.swagger;

import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Swagger2AnnotationUtils {
    public static Map<String, Object> getExtensionsFromAnnotation(Extension... extensions) {
        if (extensions == null || extensions.length < 1) {
            return null;
        }

        Map<String, Object> extensionMap = new HashMap<>();
        for (Extension extension : extensions) {
            ExtensionProperty[] properties = extension.properties();
            Optional.ofNullable(properties).ifPresent(props -> {
                for (ExtensionProperty prop : props) {
                    extensionMap.put(prop.name(), prop.value());
                }
            });
        }

        return extensionMap;
    }
}
