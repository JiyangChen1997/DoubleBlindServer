package com.cjy.doubleblindserver.common.baseclass.tool;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 15:03
 */
public class BeanCopy {

    public static void copy(Object src, Object dst) {
        BeanUtils.copyProperties(src,dst, getNullPropertyNames(src));
    }
    public static String[] getNullPropertyNames(Object source){
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for(PropertyDescriptor pd:pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null ){
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
