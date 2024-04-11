package com.lhqjlb.project.util;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
            .createXmlMapper(false)
            .failOnEmptyBeans(false)
            .failOnUnknownProperties(false)
            .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    /**
     * 对象转字符串
     */
    public static String toJsonStr(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

    /**
     * 对象转格式化字符串
     */
    public static String toJsonStrPretty(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

    /**
     * 字符串转对象
     */
    public static <T> T toBean(String str, Class<T> clazz) {
        if (StrUtil.isBlank(str) || clazz == null) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

    /**
     * 字符串转对象
     */
    public static <T> T toBean(String str, TypeReference<T> typeReference) {
        if (StrUtil.isBlank(str) || typeReference == null) {
            return null;
        }
        try {
            return objectMapper.readValue(str, typeReference);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

    /**
     * 字符串转对象
     */
    public static <T> T toBean(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

    public static <T> T convertObject(Object obj, Class<T> clazz) {
        if (obj == null || clazz == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, clazz);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

    public static <T> T convertObject(Object obj, TypeReference<T> typeReference) {
        if (obj == null || typeReference == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(obj, typeReference);
        } catch (Exception e) {
            log.error("JSON转换错误", e);
            return null;
        }
    }

}
