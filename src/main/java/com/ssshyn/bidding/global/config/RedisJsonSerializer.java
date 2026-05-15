package com.ssshyn.bidding.global.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class RedisJsonSerializer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper;

    public RedisJsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Redis 직렬화 실패", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) return null;
        try {
            return objectMapper.readValue(bytes, Object.class);
        } catch (IOException e) {
            throw new SerializationException("Redis 역직렬화 실패", e);
        }
    }
}
