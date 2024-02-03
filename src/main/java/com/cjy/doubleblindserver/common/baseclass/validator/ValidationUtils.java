package com.cjy.doubleblindserver.common.baseclass.validator;

import com.cjy.doubleblindserver.common.baseclass.customresponse.CustomException;
import com.cjy.doubleblindserver.common.baseclass.customresponse.ResponseCode;
import com.cjy.doubleblindserver.common.baseclass.customresponse.ResponseCode;
import org.apache.shiro.util.CollectionUtils;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 15:44
 */
public class ValidationUtils {

    private static Validator validator;

    static {
        validator = Validation.byDefaultProvider().configure().messageInterpolator(
                new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(getMessageSource()))
        ).buildValidatorFactory().getValidator();
    }

    private static ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setDefaultEncoding("UTF-8");
        bundleMessageSource.setBasename("i18n/validation");
        return bundleMessageSource;
    }

    public static void validateEntity(Object object, Class<?>...groups) throws CustomException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object,groups);
        if (!constraintViolations.isEmpty()) {
            ConstraintViolation<Object> constraint = constraintViolations.iterator().next();
            throw new CustomException(ResponseCode.ILLEGAL_REQUEST.getCode(),constraint.getMessage());
        }
    }

    public static void validateEntityList(List<Object> list, Class<?>...groups) throws CustomException {
        if (CollectionUtils.isEmpty(list)) {
            list.forEach(e -> {
                Set<ConstraintViolation<Object>> constraintViolations = validator.validate(e,groups);
                if(!constraintViolations.isEmpty()) {
                    ConstraintViolation<Object> constraint = constraintViolations.iterator().next();
                    throw new CustomException(ResponseCode.ILLEGAL_REQUEST.getCode(),constraint.getMessage());
                }
            });
        }
    }
}
