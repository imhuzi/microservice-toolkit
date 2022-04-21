package plus.scg.microservice.toolkit.oas.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode
public class OasMethod {

    /**
     * method path
     */
    private String path;

    /**
     * http method
     */
    private String httpMethod;

    /**
     *   method name -> operationId
     */
    private String name;

    private Set<String> headers;

    private Set<String> produces;

    private Set<String> consumes;

    List<OasParameter> parameters;

    /**
     * 返回值类型
     */
    private String returnType;
}
