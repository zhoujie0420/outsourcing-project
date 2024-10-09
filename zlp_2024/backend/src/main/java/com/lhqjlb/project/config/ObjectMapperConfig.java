package com.lhqjlb.project.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false)
                .failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                //.serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }
}
