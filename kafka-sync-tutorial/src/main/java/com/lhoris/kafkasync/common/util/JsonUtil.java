package com.lhoris.kafkasync.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lhoris.kafkasync.common.util.module.LocalDateTimeDeserializer;
import com.lhoris.kafkasync.common.util.module.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class JsonUtil {

    private JsonUtil() {}
    private static final ObjectMapper OBJ_MAP;

    static {
        OBJ_MAP = new ObjectMapper()
                .registerModule(new SimpleModule()
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                );
    }

    public static String toJson(Object obj) {
        try {
            return OBJ_MAP.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String str,  Class<T> clazz) {
        if (str != null && str.length() > 0) {
            try {
                return OBJ_MAP.readValue(str, clazz);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static <T> List<T> fromJsonList(String str, Class<T> clazz) {
        if (str != null && str.length() > 0) {
            try {
                return OBJ_MAP.readValue(str, OBJ_MAP.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return Collections.EMPTY_LIST;
    }


}
