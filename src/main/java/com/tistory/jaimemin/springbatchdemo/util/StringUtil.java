package com.tistory.jaimemin.springbatchdemo.util;

import org.springframework.util.StringUtils;

public class StringUtil {

    public static String getTextOrNull(String s) {
        return StringUtils.hasText(s) ? s : null;
    }
}
