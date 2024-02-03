package com.cjy.doubleblindserver.common.baseclass.tool;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 15:38
 */
public class StringConverter {

    public static List<Integer> String2IntegerList(String str) {
        if (StringUtils.isEmpty(str)) return new ArrayList<>();
        String[] split = str.split(",");
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            result.add(Integer.valueOf(split[i]));
        }
        return result;
    }
}
