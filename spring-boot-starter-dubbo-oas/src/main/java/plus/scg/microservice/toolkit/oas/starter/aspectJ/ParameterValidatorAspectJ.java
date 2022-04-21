package plus.scg.microservice.toolkit.oas.starter.aspectJ;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

@Aspect
@Component
@Slf4j
public class ParameterValidatorAspectJ {

    @Autowired
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    @PostConstruct
    public void init() {
    }

    /**
     * 参数校验切面
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Around("@within(org.apache.dubbo.config.annotation.DubboService) " +
            "|| @within(org.apache.dubbo.config.annotation.Service) " +
            "|| @within(com.alibaba.dubbo.config.annotation.Service)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        log.info("StartValidParam:{},{}", joinPoint.getSignature().getName(), parameters);
        if (!ObjectUtils.isEmpty(parameters)) {
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];// 形参
                Object arg = args[i]; // 实参
                BeanPropertyBindingResult beanPropertyBindingResult = new BeanPropertyBindingResult(arg, parameter.getName());
                Annotation[] annotations = parameter.getAnnotations();
                for (Annotation ann : annotations) {
                    Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
                    if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                        Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                        Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                        localValidatorFactoryBean.validate(arg, beanPropertyBindingResult, validationHints);
                        List<ObjectError> allErrors = beanPropertyBindingResult.getAllErrors();
                        if (!ObjectUtils.isEmpty(allErrors)) {
                            log.info("StartParamValidFalse:{}", allErrors);
                            throw new IllegalArgumentException(StringUtils.defaultString(allErrors.get(0).getDefaultMessage(),"Param Error"));
                        }
                        break;
                    }
                }
            }
        }
        return joinPoint.proceed();
    }
}
